
CREATE TABLE `department_payment_methods` (
  `department_id` smallint(5) DEFAULT NOT NULL,
  `payment_method` char(20) NOT NULL,
  PRIMARY KEY (`department_id`, `payment_method`),
  KEY `FK_department_payment_method` (`department_id`),
  CONSTRAINT `FK_department_payment_method` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO department_payment_methods SELECT id, 'PURCHASE_ORDER' FROM department WHERE code = 'co';
INSERT INTO department_payment_methods SELECT id, 'PURCHASE_ORDER' FROM department WHERE code = 'dh';
INSERT INTO department_payment_methods SELECT id, 'PURCHASE_ORDER' FROM department WHERE code = 'hmrc';
INSERT INTO department_payment_methods SELECT id, 'FINANCIAL_APPROVER' FROM department WHERE code = 'hmrc';
