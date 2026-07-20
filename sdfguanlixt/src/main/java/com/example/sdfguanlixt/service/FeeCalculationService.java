package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.dto.BillCalculationResult;
import com.example.sdfguanlixt.dto.TierDetail;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.entity.UsageFlow;
import com.example.sdfguanlixt.entity.Utility;
import com.example.sdfguanlixt.repository.ResidentRepository;
import com.example.sdfguanlixt.repository.UsageFlowRepository;
import com.example.sdfguanlixt.repository.UtilityRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 阶梯计价服务
 * 
 * 实现水费和电费的阶梯计价算法：
 * - 用量分段计费，每档用量使用不同倍率计算单价
 * - 阶梯划分：水费(0-15吨/15-25吨/>25吨)，电费(0-200度/200-400度/>400度)
 * - 倍率递增：用量越多，单价越高，鼓励节约资源
 * 
 * 计算公式：
 *   每档费用 = 每档用量 × (基准单价 × 该档倍率)
 *   总费用 = 各档费用累加
 */
@Service
public class FeeCalculationService {

    // 默认水费基准单价（元/吨）
    private static final double DEFAULT_WATER_PRICE = 3.5;
    
    // 默认电费基准单价（元/度）
    private static final double DEFAULT_ELECTRIC_PRICE = 0.62;

    /**
     * 水费阶梯阈值（吨）
     * 第1档上限：15吨，第2档上限：25吨，第3档无上限
     * 阶梯划分：[0, 15] / (15, 25] / (25, +∞)
     */
    private static final double[] WATER_THRESHOLDS = {15, 25};
    
    /**
     * 水费阶梯倍率
     * 第1档：1.0倍（基准价），第2档：1.2倍，第3档：1.5倍
     * 用量越高，倍率越大，单价越高
     */
    private static final double[] WATER_MULTIPLIERS = {1.0, 1.2, 1.5};
    
    /**
     * 电费阶梯阈值（度）
     * 第1档上限：200度，第2档上限：400度，第3档无上限
     * 阶梯划分：[0, 200] / (200, 400] / (400, +∞)
     */
    private static final double[] ELECTRIC_THRESHOLDS = {200, 400};
    
    /**
     * 电费阶梯倍率
     * 第1档：1.0倍（基准价），第2档：1.1倍，第3档：1.3倍
     */
    private static final double[] ELECTRIC_MULTIPLIERS = {1.0, 1.1, 1.3};

    private final UsageFlowRepository usageFlowRepository;
    private final UtilityRepository utilityRepository;
    private final ResidentRepository residentRepository;

    public FeeCalculationService(
            UsageFlowRepository usageFlowRepository,
            UtilityRepository utilityRepository,
            ResidentRepository residentRepository) {
        this.usageFlowRepository = usageFlowRepository;
        this.utilityRepository = utilityRepository;
        this.residentRepository = residentRepository;
    }

    /**
     * 计算指定住户指定账期的水电费账单
     * 
     * 计算流程：
     * 1. 获取住户信息和水电单价配置
     * 2. 查询该账期的用水量和用电量
     * 3. 分别调用阶梯计费算法计算水费和电费
     * 4. 返回账单结果（含阶梯明细）
     * 
     * @param residentId 住户ID
     * @param period     账期（如 "2026-05"）
     * @return 账单计算结果，包含用量、费用、阶梯明细等
     * @throws BusinessException 参数错误或住户不存在或无用量数据
     */
    public BillCalculationResult calculate(String residentId, String period) {
        // 参数校验
        if (!StringUtils.hasText(residentId) || !StringUtils.hasText(period)) {
            throw new BusinessException(400, BizConstants.MSG_PARAM_ERROR);
        }

        // 获取住户信息
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new BusinessException(404, BizConstants.MSG_RESIDENT_NOT_FOUND));

        // 获取住户的水电单价配置，若未配置则使用默认值
        Utility utility = utilityRepository.findFirstByResidentId(residentId).orElse(null);
        double waterBasePrice = utility != null && utility.getWaterPrice() != null
                ? utility.getWaterPrice() : DEFAULT_WATER_PRICE;
        double electricBasePrice = utility != null && utility.getElectricPrice() != null
                ? utility.getElectricPrice() : DEFAULT_ELECTRIC_PRICE;

        // 查询该账期的用量流水，汇总用水量和用电量
        List<UsageFlow> flows = usageFlowRepository.search(residentId, null, period.trim());
        double waterUsage = sumUsage(flows, BizConstants.TYPE_WATER);
        double electricUsage = sumUsage(flows, BizConstants.TYPE_ELECTRIC);

        // 若无用量数据则抛出异常
        if (waterUsage <= 0 && electricUsage <= 0) {
            throw new BusinessException(400, BizConstants.MSG_NO_USAGE_FOR_PERIOD);
        }

        // 调用阶梯计费算法分别计算水费和电费
        TierCalcResult waterCalc = calcTieredFee(
                waterUsage, waterBasePrice, WATER_THRESHOLDS, WATER_MULTIPLIERS, "水");
        TierCalcResult electricCalc = calcTieredFee(
                electricUsage, electricBasePrice, ELECTRIC_THRESHOLDS, ELECTRIC_MULTIPLIERS, "电");

        // 构建账单计算结果对象
        BillCalculationResult result = new BillCalculationResult();
        result.setResidentId(residentId);
        result.setResidentName(resident.getName());
        result.setPeriod(period.trim());
        result.setWaterUsage(round2(waterUsage));
        result.setElectricUsage(round2(electricUsage));
        result.setWaterFee(waterCalc.fee);
        result.setElectricFee(electricCalc.fee);
        result.setTotal(round2(waterCalc.fee + electricCalc.fee));
        result.setWaterTiers(waterCalc.tiers);
        result.setElectricTiers(electricCalc.tiers);
        result.setAlgorithm(
                "阶梯计价：水 0-15/≤25/>25 吨（倍率 1.0/1.2/1.5），"
                        + "电 0-200/≤400/>400 度（倍率 1.0/1.1/1.3）");
        return result;
    }

    /**
     * 阶梯计费算法核心实现：按用量区间分段累加费用
     * 
     * 算法原理：
     * 将总用量按阶梯阈值划分为多个区间，每个区间使用不同的倍率计算单价，
     * 最后将各区间的费用累加得到总费用。
     * 
     * 示例：用水量30吨，基准单价3.5元/吨，阈值[15,25]，倍率[1.0,1.2,1.5]
     *   第1档(0-15吨)：15吨 × 3.5×1.0 = 52.5元
     *   第2档(15-25吨)：10吨 × 3.5×1.2 = 42元
     *   第3档(>25吨)：5吨 × 3.5×1.5 = 26.25元
     *   总费用 = 52.5 + 42 + 26.25 = 120.75元
     * 
     * @param usage        总用量（吨或度）
     * @param basePrice    基准单价（元/吨 或 元/度）
     * @param thresholds   阶梯上限数组，长度为档数-1（最后一档无上限）
     *                     如 [15, 25] 表示第1档上限15，第2档上限25，第3档无上限
     * @param multipliers  各档倍率数组，长度 = thresholds.length + 1
     *                     如 [1.0, 1.2, 1.5] 表示第1档1倍，第2档1.2倍，第3档1.5倍
     * @param typeLabel    类型标签（"水" 或 "电"），用于生成档位名称
     * @return TierCalcResult 包含总费用和各档明细列表
     */
    static TierCalcResult calcTieredFee(
            double usage,
            double basePrice,
            double[] thresholds,
            double[] multipliers,
            String typeLabel) {
        
        // 初始化档位明细列表
        List<TierDetail> tiers = new ArrayList<>();
        
        // 边界情况：用量或单价为0时，直接返回0费用
        if (usage <= 0 || basePrice <= 0) {
            return new TierCalcResult(0, tiers);
        }

        double totalFee = 0;   // 累计总费用
        double prev = 0;       // 上一档的上限值，用于计算当前档用量
        
        // 遍历每个档位进行分段计算
        for (int i = 0; i < multipliers.length; i++) {
            // 确定当前档的上限：最后一档无上限，使用Double.MAX_VALUE
            double upper = i < thresholds.length ? thresholds[i] : Double.MAX_VALUE;
            
            // 计算当前档的实际用量
            // 公式：min(总用量, 当前档上限) - 上一档上限
            // 这样可以确保：1)不超过总用量；2)不重复计算已计算过的用量
            double tierUsage = Math.min(usage, upper) - prev;
            
            // 当前档用量为0时，说明总用量已全部处理完毕，退出循环
            if (tierUsage <= 0) {
                break;
            }
            
            // 计算当前档单价 = 基准单价 × 该档倍率
            double unitPrice = round4(basePrice * multipliers[i]);
            
            // 计算当前档费用 = 当前档用量 × 当前档单价
            double tierFee = round2(tierUsage * unitPrice);
            
            // 累加到总费用
            totalFee += tierFee;

            // 构建档位名称（如 "水 第1档(≤15)"）
            String rangeLabel = buildTierLabel(i, thresholds, typeLabel);
            
            // 添加档位明细到列表
            tiers.add(new TierDetail(rangeLabel, round2(tierUsage), unitPrice, tierFee));
            
            // 更新上一档上限，为下一档计算做准备
            prev = upper;
            
            // 如果总用量已处理完毕（不超过当前档上限），退出循环
            if (usage <= upper) {
                break;
            }
        }
        
        // 返回计算结果：总费用（四舍五入保留2位小数）和档位明细列表
        return new TierCalcResult(round2(totalFee), tiers);
    }

    /**
     * 构建档位名称标签
     * 
     * @param index      档位索引（0表示第1档，1表示第2档...）
     * @param thresholds 阶梯阈值数组
     * @param typeLabel  类型标签（"水" 或 "电"）
     * @return 档位名称，如 "水 第1档(≤15)"、"水 第2档(15-25)"、"水 第3档(>25)"
     */
    private static String buildTierLabel(int index, double[] thresholds, String typeLabel) {
        // 第1档：显示为 "类型 第1档(≤第一阈值)"
        if (index == 0) {
            return typeLabel + " 第1档(≤" + formatNum(thresholds[0]) + ")";
        }
        // 中间档（非最后一档）：显示为 "类型 第N档(上一阈值-当前阈值)"
        if (index < thresholds.length) {
            return typeLabel + " 第" + (index + 1) + "档("
                    + formatNum(thresholds[index - 1]) + "-" + formatNum(thresholds[index]) + ")";
        }
        // 最后一档：显示为 "类型 第N档(>最后阈值)"
        return typeLabel + " 第" + (index + 1) + "档(>" + formatNum(thresholds[thresholds.length - 1]) + ")";
    }

    /**
     * 格式化数字：整数显示为整数，小数保留原样
     * 
     * @param v 待格式化的数字
     * @return 格式化后的字符串，如 15.0 显示为 "15"，15.5 显示为 "15.5"
     */
    private static String formatNum(double v) {
        if (v == (long) v) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    /**
     * 汇总指定类型的用量
     * 
     * @param flows 用量流水列表
     * @param type  类型（"水" 或 "电"）
     * @return 该类型用量的总和
     */
    private double sumUsage(List<UsageFlow> flows, String type) {
        return flows.stream()
                .filter(f -> type.equals(f.getType()))  // 过滤出指定类型的流水
                .mapToDouble(f -> f.getUsage() != null ? f.getUsage() : 0)  // 获取用量，空值视为0
                .sum();  // 求和
    }

    /**
     * 四舍五入保留2位小数
     * 用于金额计算结果的精度处理
     * 
     * @param v 待处理的数值
     * @return 四舍五入后保留2位小数的结果
     */
    static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /**
     * 四舍五入保留4位小数
     * 用于单价计算结果的精度处理（单价需要更高精度避免累积误差）
     * 
     * @param v 待处理的数值
     * @return 四舍五入后保留4位小数的结果
     */
    private static double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    /**
     * 阶梯计费计算结果内部类
     * 用于封装总费用和档位明细列表
     */
    static class TierCalcResult {
        final double fee;           // 总费用
        final List<TierDetail> tiers; // 各档明细列表
        TierCalcResult(double fee, List<TierDetail> tiers) {
            this.fee = fee;
            this.tiers = tiers;
        }
    }
}
