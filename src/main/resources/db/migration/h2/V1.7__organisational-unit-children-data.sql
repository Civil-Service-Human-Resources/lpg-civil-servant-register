INSERT INTO `organisational_unit` (`id`, `parent_id`, `code`, `abbreviation`, `name`) VALUES
(4, 1, 'D1.1', 'COC1', 'Cabinet Office Child 1'),
(5, 1, 'D1.2', 'COC2', 'Cabinet Office Child 2'),
(6, 5, 'D1.2.1', 'COGC1', 'Cabinet Office Grandchild 1'),
(7, 3, 'D3.1', 'HMRCC1', 'HMRC Child 1'),
(8, 7, 'D3.1.1', 'HMRCGC1', 'HMRC Grandchild 1'),
(9, 7, 'D3.2', 'HMRCC2', 'HMRC Child 2');