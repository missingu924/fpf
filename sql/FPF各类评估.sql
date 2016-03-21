/* Formatted on 2013/1/22 10:51:52 (QP5 v5.114.809.3010) */
--数据源评估：FTP连接成功率、文件生成完整率、文件生成及时率

CREATE OR REPLACE VIEW NWOM.FPF_EVAL_OMC_SOURCE AS
  SELECT
           '北京' "地市",
           "域",
           "制式",
           "厂商",
           t00.OMC,
           "数据类型",
           "时间类型",
           "数据开始时间",
           "数据结束时间",
           "理论文件总数",
           "生成文件数",
           "文件生成完整率（%）",
           "及时生成文件数",
           "文件生成及时率（%）",
           "FTP连接尝试次数",
           "FTP连接成功次数",
           "FTP连接成功率（%）"
    FROM      (  SELECT   "域",
                          "制式",
                          "厂商",
                          OMC,
                          "数据类型",
                          "时间类型",
                          "数据开始时间",
                          "数据结束时间",
                          (SELECT   theoretic_file_count
                             FROM   nwom.fpf_omc_standard t1
                            WHERE       t0.omc = t1.omc
                                    AND t0."数据类型" = t1.data_type
                                    AND t0."时间类型" = t1.time_type)
                             "理论文件总数",
                          COUNT ( * ) "生成文件数",
                          ROUND (
                             COUNT ( * )
                             / (SELECT   theoretic_file_count
                                  FROM   nwom.fpf_omc_standard t1
                                 WHERE       t0.omc = t1.omc
                                         AND t0."数据类型" = t1.data_type
                                         AND t0."时间类型" = t1.time_type)
                             * 100,
                             2
                          )
                             "文件生成完整率（%）",
                          SUM (DECODE ("文件生成延误", '是', 0, 1))
                             "及时生成文件数",
                          ROUND (
                             100
                             -   SUM (DECODE ("文件生成延误", '是', 1, 0))
                               / COUNT ( * )
                               * 100,
                             2
                          )
                             "文件生成及时率（%）"
                   FROM   nwom.fpf_delay_detail t0
               GROUP BY   "域",
                          "制式",
                          "厂商",
                          OMC,
                          "数据类型",
                          "时间类型",
                          "数据开始时间",
                          "数据结束时间") T00
           LEFT JOIN
              (  SELECT   ftp_server_name OMC,
                          TRUNC (start_time, 'HH24') "时间",
                          COUNT ( * ) "FTP连接尝试次数",
                          SUM (DECODE (handle_state, '结束', 1, 0))
                             "FTP连接成功次数",
                          ROUND (
                               SUM (DECODE (handle_state, '结束', 1, 0))
                             / COUNT ( * )
                             * 100,
                             2
                          )
                             "FTP连接成功率（%）"
                   FROM   nwom.fpf_ftp_connect
               GROUP BY   ftp_server_name, TRUNC (start_time, 'HH24')) T01
           ON T00.OMC = T01.OMC
              AND TRUNC (T00."数据开始时间", 'HH24') = T01."时间"
ORDER BY   "域",
           "制式",
           "数据开始时间" DESC,
           "厂商" DESC,
           "数据类型";



--采集程序评估：采集完整性、采集及时性

CREATE OR REPLACE VIEW NWOM.FPF_EVAL_OMC_COLLECT AS
  SELECT   '北京' "地市",
           "域",
           "制式",
           "厂商",
           OMC,
           "数据类型",
           "时间类型",
           "数据开始时间",
           "数据结束时间",
           (SELECT   theoretic_file_count
              FROM   nwom.fpf_omc_standard t1
             WHERE       t0.omc = t1.omc
                     AND t0."数据类型" = t1.data_type
                     AND t0."时间类型" = t1.time_type)
              "理论文件总数",
           COUNT ( * ) "采集文件数",
           ROUND (
              COUNT ( * )
              / (SELECT   theoretic_file_count
                   FROM   nwom.fpf_omc_standard t1
                  WHERE       t0.omc = t1.omc
                          AND t0."数据类型" = t1.data_type
                          AND t0."时间类型" = t1.time_type)
              * 100,
              2
           )
              "文件采集完整率（%）",
           SUM (DECODE ("文件采集延误", '是', 0, 1)) "及时采集文件数",
           ROUND (
              100
              - SUM (DECODE ("文件采集延误", '是', 1, 0)) / COUNT ( * ) * 100,
              2
           )
              "文件采集及时率（%）"
    FROM   nwom.fpf_delay_detail t0
GROUP BY   "域",
           "制式",
           "厂商",
           OMC,
           "数据类型",
           "时间类型",
           "数据开始时间",
           "数据结束时间"
ORDER BY   "域",
           "制式",
           "数据开始时间" DESC,
           "厂商" DESC,
           "数据类型";

--解析程序评估：解析完整性、解析及时性、解析成功率

CREATE OR REPLACE VIEW NWOM.FPF_EVAL_OMC_PARSE AS
  SELECT     '北京' "地市",
           "域",
           "制式",
           "厂商",
           OMC,
           "数据类型",
           "时间类型",
           "数据开始时间",
           "数据结束时间",
           (SELECT   theoretic_file_count
              FROM   nwom.fpf_omc_standard t1
             WHERE       t0.omc = t1.omc
                     AND t0."数据类型" = t1.data_type
                     AND t0."时间类型" = t1.time_type)
              "理论文件总数",
           COUNT ( * ) "解析文件数",
           ROUND (
              COUNT ( * )
              / (SELECT   theoretic_file_count
                   FROM   nwom.fpf_omc_standard t1
                  WHERE       t0.omc = t1.omc
                          AND t0."数据类型" = t1.data_type
                          AND t0."时间类型" = t1.time_type)
              * 100,
              2
           )
              "文件解析完整率（%）",
           SUM (DECODE ("文件解析延误", '是', 0, 1)) "及时解析文件数",
           ROUND (
              100
              - SUM (DECODE ("文件解析延误", '是', 1, 0)) / COUNT ( * ) * 100,
              2
           )
              "文件解析及时率（%）",
           SUM (DECODE ("结束状态", '出错', 1, 0)) "解析出错文件数",
           --ROUND (SUM (DECODE ("结束状态", '结束', 1, 0)) / COUNT ( * ) * 100, 2) "解析成功率（%）",
           SUM ("成功行数") "解析成功行数",
           SUM ("失败行数") "解析失败行数",
           ROUND (SUM ("成功行数") / SUM ("成功行数" + "失败行数") * 100, 2)
              "解析成功率（%）"
    FROM   nwom.fpf_delay_detail t0
GROUP BY   "域",
           "制式",
           "厂商",
           OMC,
           "数据类型",
           "时间类型",
           "数据开始时间",
           "数据结束时间"
ORDER BY   "域",
           "制式",
           "数据开始时间" DESC,
           "厂商" DESC,
           "数据类型";

--汇总评估：待完善