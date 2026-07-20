INSERT INTO resident (id, name, phone, building, unit, type, move_in_date) VALUES
('R001', '张三', '13800138001', '1栋', '101', '业主', '2020-03-15'),
('R002', '李四', '13800138002', '1栋', '202', '租户', '2022-06-01'),
('R003', '王五', '13800138003', '2栋', '301', '业主', '2019-11-20');

INSERT INTO utility (id, resident_id, resident_name, water_price, electric_price, water_base, electric_base, remark) VALUES
('U001', 'R001', '张三', 3.5, 0.62, 0, 0, ''),
('U002', 'R002', '李四', 3.5, 0.62, 15, 50, '含公摊'),
('U003', 'R003', '王五', 3.8, 0.65, 0, 0, '');

INSERT INTO meter_reading (id, resident_id, resident_name, period, water_reading, electric_reading, read_date, operator) VALUES
('M001', 'R001', '张三', '2026-04', 125.5, 856.2, '2026-04-28', '管理员'),
('M002', 'R002', '李四', '2026-04', 88.3, 420.1, '2026-04-28', '管理员');

INSERT INTO usage_flow (id, resident_id, resident_name, type, usage_amount, period, amount, create_time) VALUES
('F001', 'R001', '张三', '水', 5.2, '2026-04', 18.2, '2026-04-28 10:00'),
('F002', 'R001', '张三', '电', 68.5, '2026-04', 42.47, '2026-04-28 10:00'),
('F003', 'R002', '李四', '水', 4.1, '2026-04', 14.35, '2026-04-28 10:05'),
('F004', 'R002', '李四', '电', 56.8, '2026-04', 35.2, '2026-04-28 10:05'),
('F005', 'R001', '张三', '水', 96.5, '2026-05', 469.88, '2026-05-28 10:00'),
('F006', 'R001', '张三', '电', 150.0, '2026-05', 93.0, '2026-05-28 10:00'),
('F007', 'R002', '李四', '水', 85.3, '2026-05', 384.45, '2026-05-28 10:05'),
('F008', 'R002', '李四', '电', 120.5, '2026-05', 74.71, '2026-05-28 10:05');

INSERT INTO payment_order (id, resident_id, resident_name, period, water_fee, electric_fee, total_amount, status, pay_time, create_time) VALUES
('O001', 'R001', '张三', '2026-04', 18.2, 42.47, 60.67, '已缴费', '2026-05-05 14:30', '2026-05-01 09:00'),
('O002', 'R002', '李四', '2026-04', 14.35, 35.2, 49.55, '待缴费', NULL, '2026-05-01 09:00');

INSERT INTO sys_user (id, username, password, role, resident_id, name) VALUES
('admin', 'admin', 'admin123', 'ADMIN', NULL, '系统管理员'),
('u001', '13800138001', '123456', 'RESIDENT', 'R001', '张三'),
('u002', '13800138002', '123456', 'RESIDENT', 'R002', '李四');
