package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.common.TimeUtil;
import com.example.sdfguanlixt.dto.PayRequest;
import com.example.sdfguanlixt.dto.PayResult;
import com.example.sdfguanlixt.entity.PaymentOrder;
import com.example.sdfguanlixt.repository.PaymentOrderRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PaymentSimulationService {

    private static final String DEMO_PAY_PASSWORD = "123456";
    private static final Map<String, String> PAY_METHOD_LABELS = Map.of(
            "wechat", "\u5fae\u4fe1\u652f\u4ed8",
            "alipay", "\u652f\u4ed8\u5b9d",
            "bank", "\u94f6\u884c\u5361");

    private final PaymentOrderRepository orderRepository;

    public PaymentSimulationService(PaymentOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PayResult simulatePay(String orderId, PayRequest request) {
        PaymentOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, BizConstants.MSG_ORDER_NOT_FOUND));

        List<String> steps = new ArrayList<>();
        steps.add("\u3010\u6b65\u9aa41\u3011\u6821\u9a8c\u8ba2\u5355\u72b6\u6001");
        if (BizConstants.STATUS_PAID.equals(order.getStatus())) {
            throw new BusinessException(400, BizConstants.MSG_ORDER_ALREADY_PAID);
        }

        double amount = order.getTotal() != null ? order.getTotal() : 0;
        steps.add("\u3010\u6b65\u9aa42\u3011\u91d1\u989d\u6821\u9a8c\u901a\u8fc7\uff0c\u5e94\u7f34 \u00a5" + formatMoney(amount));

        String payMethod = normalizePayMethod(request.getPayMethod());
        String payMethodLabel = PAY_METHOD_LABELS.getOrDefault(payMethod, payMethod);
        steps.add("\u3010\u6b65\u9aa43\u3011\u652f\u4ed8\u901a\u9053\uff1a" + payMethodLabel);

        steps.add("\u3010\u6b65\u9aa44\u3011\u9a8c\u8bc1\u652f\u4ed8\u5bc6\u7801");
        validatePayPassword(request.getPayPassword());

        String transactionNo = generateTransactionNo(order, payMethod);
        String verifyCode = generateVerifyCode(amount, transactionNo);
        steps.add("\u3010\u6b65\u9aa45\u3011\u751f\u6210\u4ea4\u6613\u5355\u53f7\uff1a" + transactionNo);
        steps.add("\u3010\u6b65\u9aa46\u3011\u6821\u9a8c\u7801\uff1a" + verifyCode + " \uff08\u91d1\u989d\u6821\u9a8c\u901a\u8fc7\uff09");

        String payTime = TimeUtil.nowDateTime();
        order.setStatus(BizConstants.STATUS_PAID);
        order.setPayTime(payTime);
        PaymentOrder saved = orderRepository.save(order);

        steps.add("\u3010\u6b65\u9aa47\u3011\u652f\u4ed8\u6210\u529f\uff0c\u8ba2\u5355\u5df2\u66f4\u65b0\u4e3a\u5df2\u7f34\u8d39");

        PayResult result = new PayResult();
        result.setOrder(saved);
        result.setTransactionNo(transactionNo);
        result.setPayMethod(payMethod);
        result.setPayMethodLabel(payMethodLabel);
        result.setAmount(amount);
        result.setPayTime(payTime);
        result.setVerifyCode(verifyCode);
        result.setSteps(steps);
        return result;
    }

    private void validatePayPassword(String password) {
        if (!DEMO_PAY_PASSWORD.equals(password)) {
            throw new BusinessException(400, BizConstants.MSG_PAY_PASSWORD_ERROR);
        }
    }

    private String normalizePayMethod(String payMethod) {
        if (!StringUtils.hasText(payMethod)) {
            throw new BusinessException(400, BizConstants.MSG_PARAM_ERROR);
        }
        String method = payMethod.trim().toLowerCase(Locale.ROOT);
        if (!PAY_METHOD_LABELS.containsKey(method)) {
            throw new BusinessException(400, BizConstants.MSG_PAY_METHOD_INVALID);
        }
        return method;
    }

    /**
     * 交易单号生成算法：TXN + 时间戳 + 订单哈希 + 随机数
     */
    String generateTransactionNo(PaymentOrder order, String payMethod) {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int orderHash = Math.abs((order.getId() + payMethod).hashCode()) % 10000;
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("TXN%s%04d%d", timePart, orderHash, random);
    }

    /**
     * 金额校验码算法：对 (金额*100 + 交易号数字后缀) 取模 9973
     */
    String generateVerifyCode(double amount, String transactionNo) {
        long cents = Math.round(amount * 100);
        long suffix = 0;
        for (int i = transactionNo.length() - 4; i < transactionNo.length(); i++) {
            if (i >= 0 && Character.isDigit(transactionNo.charAt(i))) {
                suffix = suffix * 10 + (transactionNo.charAt(i) - '0');
            }
        }
        long code = (cents + suffix) % 9973;
        return String.format("%04d", code);
    }

    private String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }
}
