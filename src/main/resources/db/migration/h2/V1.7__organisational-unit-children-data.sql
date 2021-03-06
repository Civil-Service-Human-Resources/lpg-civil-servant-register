INSERT INTO `organisational_unit` (`id`, `parent_id`, `code`, `abbreviation`, `name`) VALUES
(4, 2, 'D21', 'DHC', 'DH Core'),
(5, 2, 'D22', 'HRA', 'Health Research Authority'),
(6, 2, 'D23', 'HFE', 'Human Fertilisation & Embryology'),
(7, 2, 'D24', 'MHPRA', 'Medicines and Healthcare Products Regulatory Agency'),
(8, 2, 'D25', 'PHE', 'Public Health England'),
(9, 4, 'D211', 'ACW', 'Acute Care and Workforce'),
(10, 4, 'D212', 'CSCC', 'Community and Social Care Commercial'),
(11, 4, 'D213', 'FNHS', 'F&NHS'),
(12, 4, 'D214', 'FGO', 'Finance and Group Operations'),
(13, 4, 'D215', 'GPH', 'Global and Public Health'),
(14, 4, 'D216', 'IGO', 'IGO'),
(15, 4, 'D217', 'PHD', 'PHD'),
(16, 4, 'D218', 'RDD', 'RDD'),
(17, 4, 'D219', 'GCLGCP', 'GCLGCP'),
(18, 4, 'D2110', 'SER', 'SER'),
(19, 7, 'D241', 'CMS', 'Communications'),
(20, 7, 'D242', 'CPRD', 'CPRD'),
(21, 7, 'D243', 'DVS', 'Devices'),
(22, 7, 'D244', 'DCRTE', 'Directorate'),
(23, 7, 'D245', 'F&P', 'Finance & Procurement'),
(24, 7, 'D246', 'HR', 'HR'),
(25, 7, 'D247', 'IE&S', 'IE&S'),
(26, 7, 'D248', 'IMD', 'IMD'),
(27, 7, 'D249', 'LCSING', 'Licensing'),
(28, 7, 'D2410', 'NIBSC', 'NIBSC'),
(29, 7, 'D2411', 'PLCY', 'Policy'),
(30, 7, 'D2412', 'VRMM', 'VRMM'),
(31, null, 'parent', 'parent', 'parent'),
(32, 31, 'child', 'child', 'child'),
(33, 32, 'grandchild', 'grandchild', 'grandchild')
;