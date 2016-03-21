/* Formatted on 2013/1/4 13:30:42 (QP5 v5.114.809.3010) */
DROP TABLE NWOM.FPF_PARSER_MAIN;
DROP TABLE NWOM.FPF_PARSER_MIDDLE;
DROP TABLE NWOM.FPF_PARSER_SQLLDR;
DROP TABLE NWOM.FPF_FTP_CONNECT;
DROP TABLE NWOM.FPF_FTP_NESTDL;
DROP TABLE NWOM.FPF_FTP_ONEFILEDL;

CREATE TABLE NWOM.FPF_PARSER_MAIN
(
   CITY_NAME                 VARCHAR (128) DEFAULT '北京',
   DOMAIN                    VARCHAR (128),                          --域/专业
   TECHNOLOGY                VARCHAR (128),                             --制式
   VENDOR                    VARCHAR (128),                             --厂商
   DATA_TYPE                 VARCHAR (128),                         --数据类型
   BUSINESS_TYPE             VARCHAR (128),                         --业务类型
   NE_TYPE                   VARCHAR (128),                     --上报网元粒度
   TIME_TYPE                 VARCHAR (128),                     --上报时间周期
   NE_NAME                   VARCHAR (128),                         --网元名称
   REPORT_TIME               DATE,                              --文件报告时间
   DATA_START_TIME           DATE,                              --数据开始时间
   DATA_END_TIME             DATE,                              --数据结束时间
   HANDLE_NAME               VARCHAR (256),                         --处理环节
   HANDLE_STATE              VARCHAR (128),                         --处理状态
   SRC_FILE_PATH             VARCHAR (1024),                          --源文件
   SRC_FILE_LENGTH           NUMBER (32),                   --源文件大小(Byte)
   SRC_FILE_LASTMODIFYTIME   DATE,                        --源文件最后修改时间
   START_TIME                DATE,                          --本次处理开始时间
   END_TIME                  DATE,                          --本次处理结束时间
   USEDTIME_MILLIS           NUMBER (32),                 --本次处理耗时(毫秒)
   ERROR_MESSAGE             VARCHAR (4000),                --本次处理错误信息
   SUCC_ROWS                 NUMBER (32),                           --成功条数
   ERROR_ROWS                NUMBER (32),                           --失败条数
   TIME_STAMP                DATE DEFAULT SYSDATE                     --时间戳
);

CREATE INDEX NWOM.FPF_PARSER_MAIN_idx00 ON NWOM.FPF_PARSER_MAIN (DATA_START_TIME)L;

CREATE INDEX NWOM.FPF_PARSER_MAIN_IDX01 ON NWOM.FPF_PARSER_MAIN (SRC_FILE_PATH);

CREATE INDEX NWOM.FPF_PARSER_MAIN_IDX02 ON NWOM.FPF_PARSER_MAIN (START_TIME);

CREATE TABLE NWOM.FPF_PARSER_MIDDLE
(
   CITY_NAME                 				VARCHAR (128) DEFAULT '北京',
   DOMAIN                           VARCHAR (128),                   --域/专业
   TECHNOLOGY                       VARCHAR (128),                      --制式
   VENDOR                           VARCHAR (128),                      --厂商
   DATA_TYPE                        VARCHAR (128),                  --数据类型
   BUSINESS_TYPE                    VARCHAR (128),                  --业务类型
   NE_TYPE                          VARCHAR (128),              --上报网元粒度
   TIME_TYPE                        VARCHAR (128),              --上报时间周期
   NE_NAME                          VARCHAR (128),                  --网元名称
   REPORT_TIME                      DATE,                       --文件报告时间
   DATA_START_TIME                  DATE,                       --数据开始时间
   DATA_END_TIME                    DATE,                       --数据结束时间
   HANDLE_NAME                      VARCHAR (256),                  --处理环节
   HANDLE_STATE                     VARCHAR (128),                  --处理状态
   SRC_FILE_PATH                    VARCHAR (1024),                   --源文件
   SRC_FILE_LENGTH                  NUMBER (32),            --源文件大小(Byte)
   SRC_FILE_LASTMODIFYTIME          DATE,                 --源文件最后修改时间
   START_TIME                       DATE,                   --本次处理开始时间
   END_TIME                         DATE,                   --本次处理结束时间
   USEDTIME_MILLIS                  NUMBER (32),          --本次处理耗时(毫秒)
   ERROR_MESSAGE                    VARCHAR (4000),         --本次处理错误信息
   NEEDHANDLE_FILE_PATH             VARCHAR (1024),               --待处理文件
   NEEDHANDLE_FILE_LENGTH           NUMBER (32),        --待处理文件大小(Byte)
   NEEDHANDLE_FILE_LASTMODIFYTIME   DATE,             --待处理文件最后修改时间
   HANDLED_FILE_PATH                VARCHAR (1024),               --处理后文件
   HANDLED_FILE_LENGTH              NUMBER (32),        --处理后文件大小(Byte)
   HANDLED_FILE_LASTMODIFYTIME      DATE,             --处理后文件最后修改时间
   TIME_STAMP                       DATE DEFAULT SYSDATE              --时间戳
);

CREATE INDEX NWOM.FPF_PARSER_MIDDLE_IDX01 ON NWOM.FPF_PARSER_MIDDLE (SRC_FILE_PATH);

CREATE INDEX NWOM.FPF_PARSER_MIDDLE_IDX02 ON NWOM.FPF_PARSER_MIDDLE (START_TIME);

CREATE TABLE NWOM.FPF_PARSER_SQLLDR
(
   CITY_NAME                  VARCHAR (128) DEFAULT '北京',
   DOMAIN                     VARCHAR (128),                         --域/专业
   TECHNOLOGY                 VARCHAR (128),                            --制式
   VENDOR                     VARCHAR (128),                            --厂商
   DATA_TYPE                  VARCHAR (128),                        --数据类型
   BUSINESS_TYPE              VARCHAR (128),                        --业务类型
   NE_TYPE                    VARCHAR (128),                    --上报网元粒度
   TIME_TYPE                  VARCHAR (128),                    --上报时间周期
   NE_NAME                    VARCHAR (128),                        --网元名称
   REPORT_TIME                DATE,                             --文件报告时间
   DATA_START_TIME            DATE,                             --数据开始时间
   DATA_END_TIME              DATE,                             --数据结束时间
   HANDLE_NAME                VARCHAR (256),                        --处理环节
   HANDLE_STATE               VARCHAR (128),                        --处理状态
   SRC_FILE_PATH              VARCHAR (1024),                         --源文件
   SRC_FILE_LENGTH            NUMBER (32),                  --源文件大小(Byte)
   SRC_FILE_LASTMODIFYTIME    DATE,                       --源文件最后修改时间
   START_TIME                 DATE,                         --本次处理开始时间
   END_TIME                   DATE,                         --本次处理结束时间
   USEDTIME_MILLIS            NUMBER (32),                --本次处理耗时(毫秒)
   ERROR_MESSAGE              VARCHAR (4000),               --本次处理错误信息
   TABLE_NAME                 VARCHAR (128),                        --对应表名
   DATA_FILE_PATH             VARCHAR (1024),                     --待入库文件
   DATA_FILE_LENGTH           NUMBER (32),              --待入库文件大小(Byte)
   DATA_FILE_LASTMODIFYTIME   DATE,                   --待入库文件最后修改时间
   SUCC_ROWS                  NUMBER (32),                          --成功条数
   ERROR_ROWS                 NUMBER (32),                          --失败条数
   TIME_STAMP                 DATE DEFAULT SYSDATE                   --时间戳
);

CREATE INDEX NWOM.FPF_PARSER_SQLLDR_IDX01 ON NWOM.FPF_PARSER_SQLLDR (SRC_FILE_PATH, DATA_FILE_PATH);

CREATE INDEX NWOM.FPF_PARSER_SQLLDR_IDX02 ON NWOM.FPF_PARSER_SQLLDR (START_TIME);

CREATE TABLE NWOM.FPF_FTP_CONNECT
(
   DOMAIN            VARCHAR (128),                                  --域/专业
   TECHNOLOGY        VARCHAR (128),                                     --制式
   VENDOR            VARCHAR (128),                                     --厂商
   DATA_TYPE         VARCHAR (128),                                 --数据类型
   BUSINESS_TYPE     VARCHAR (128),                                 --业务类型
   NE_TYPE           VARCHAR (128),                             --上报网元粒度
   TIME_TYPE         VARCHAR (128),                             --上报时间周期
   NE_NAME           VARCHAR (128),                                 --网元名称
   REPORT_TIME       DATE,                                      --文件报告时间
   HANDLE_NAME       VARCHAR (256),                                 --处理环节
   HANDLE_STATE      VARCHAR (128),                                 --处理状态
   FTP_SERVER_NAME   VARCHAR (1024),                             --FTP服务器名
   START_TIME        DATE,                                  --本次处理开始时间
   END_TIME          DATE,                                  --本次处理结束时间
   ERROR_MESSAGE     VARCHAR (4000),                        --本次处理错误信息
   TIME_STAMP        DATE DEFAULT SYSDATE                             --时间戳
);

CREATE INDEX NWOM.FPF_FTP_CONNECT_IDX01 ON NWOM.FPF_FTP_CONNECT (START_TIME, FTP_SERVER_NAME);

CREATE TABLE NWOM.FPF_FTP_NESTDL
(
   DOMAIN               VARCHAR (128),                               --域/专业
   TECHNOLOGY           VARCHAR (128),                                  --制式
   VENDOR               VARCHAR (128),                                  --厂商
   DATA_TYPE            VARCHAR (128),                              --数据类型
   BUSINESS_TYPE        VARCHAR (128),                              --业务类型
   NE_TYPE              VARCHAR (128),                          --上报网元粒度
   TIME_TYPE            VARCHAR (128),                          --上报时间周期
   NE_NAME              VARCHAR (128),                              --网元名称
   REPORT_TIME          DATE,                                   --文件报告时间
   HANDLE_NAME          VARCHAR (256),                              --处理环节
   HANDLE_STATE         VARCHAR (128),                              --处理状态
   FTP_SERVER_NAME      VARCHAR (1024),                          --FTP服务器名
   FTP_DIR              VARCHAR (1024),                              --FTP目录
   FTP_FILTER           VARCHAR (1024),                          --FTP过滤条件
   FTP_DL_FILES_COUNT   NUMBER (32),                          --本次下载文件数
   START_TIME           DATE,                               --本次处理开始时间
   END_TIME             DATE,                               --本次处理结束时间
   USEDTIME_MILLIS      NUMBER (32),                      --本次处理耗时(毫秒)
   ERROR_MESSAGE        VARCHAR (4000),                     --本次处理错误信息
   TIME_STAMP           DATE DEFAULT SYSDATE                          --时间戳
);

CREATE INDEX NWOM.FPF_FTP_NESTDL_IDX01 ON NWOM.FPF_FTP_NESTDL (START_TIME, FTP_SERVER_NAME);

CREATE TABLE NWOM.FPF_FTP_ONEFILEDL
(
   DOMAIN                     VARCHAR (128),                         --域/专业
   TECHNOLOGY                 VARCHAR (128),                            --制式
   VENDOR                     VARCHAR (128),                            --厂商
   DATA_TYPE                  VARCHAR (128),                        --数据类型
   BUSINESS_TYPE              VARCHAR (128),                        --业务类型
   NE_TYPE                    VARCHAR (128),                    --上报网元粒度
   TIME_TYPE                  VARCHAR (128),                    --上报时间周期
   NE_NAME                    VARCHAR (128),                        --网元名称
   REPORT_TIME                DATE,                             --文件报告时间
   DATA_START_TIME            DATE,                             --数据开始时间
   DATA_END_TIME              DATE,                             --数据结束时间
   HANDLE_NAME                VARCHAR (256),                        --处理环节
   HANDLE_STATE               VARCHAR (128),                        --处理状态
   FTP_SERVER_NAME            VARCHAR (256),                     --FTP服务器名
   SRC_FILE_PATH              VARCHAR (1024),                         --源文件
   SRC_FILE_LENGTH            NUMBER (32),                  --源文件大小(Byte)
   SRC_FILE_LASTMODIFYTIME    DATE,                       --源文件最后修改时间
   START_TIME                 DATE,                         --本次处理开始时间
   END_TIME                   DATE,                         --本次处理结束时间
   USEDTIME_MILLIS            NUMBER (32),                --本次处理耗时(毫秒)
   ERROR_MESSAGE              VARCHAR (4000),               --本次处理错误信息
   DEST_FILE_PATH             VARCHAR (1024),                       --目的文件
   DEST_FILE_LENGTH           NUMBER (32),                --目的文件大小(Byte)
   DEST_FILE_LASTMODIFYTIME   DATE,                     --目的文件最后修改时间
   TIME_STAMP                 DATE DEFAULT SYSDATE                    --时间戳
);

CREATE INDEX NWOM.FPF_FTP_ONEFILEDL_IDX01 ON NWOM.FPF_FTP_ONEFILEDL (START_TIME, FTP_SERVER_NAME, SRC_FILE_PATH, DEST_FILE_PATH);
                              
CREATE TABLE NWOM.FPF_OMC_STANDARD
(
   DOMAIN                    VARCHAR (128),                          --域/专业
   TECHNOLOGY                VARCHAR (128),                             --制式
   VENDOR                    VARCHAR (128),                             --厂商
   DATA_TYPE                 VARCHAR (128),                         --数据类型
   TIME_TYPE                 VARCHAR (128),                     --上报时间周期
   OMC                       VARCHAR (128),                              --OMC
   RNC_BSC_COUNT             INT,                               --RNC或BSC数量
   THEORETIC_FILE_COUNT      NUMBER (32),                         --理论文件数
   THEORETIC_ROWS            NUMBER (32),                       --理论数据条数
   ALLOWED_MAX_FILE_DELAY    INT,               --允许的最大文件生成时延(分钟)
   ALLOWED_MAX_DL_DELAY      INT,                   --允许的最大采集时延(分钟)
   ALLOWED_MAX_PARSE_DELAY   INT,                   --允许的最大解析时延(分钟)
   ALLOWED_MAX_DATA_DELAY    INT                  --允许的最大数据总时延(分钟)
);
                             




--时延统计：单文件时延详情
CREATE OR REPLACE VIEW nwom.fpf_delay_detail
AS
SELECT t0.*,
CASE WHEN ((t0."数据总时延（分）"-t1.ALLOWED_MAX_DATA_DELAY) >0) THEN '是' ELSE
      ''
      END AS "数据延误",
CASE WHEN ((t0."文件生成时延（分）"-t1.ALLOWED_MAX_FILE_DELAY) >0) THEN '是'
      ELSE ''
      END AS "文件生成延误",
CASE WHEN ((t0."文件采集时延（分）"-t1.ALLOWED_MAX_DL_DELAY) >0) THEN '是' ELSE
      ''
      END AS "文件采集延误",
CASE WHEN ((t0."文件解析时延（分）"-t1.ALLOWED_MAX_PARSE_DELAY) >0) THEN '是'
      ELSE ''
      END AS "文件解析延误"
FROM
(
    SELECT
    '北京' "地市",
    domain "域",
    technology "制式",
    vendor "厂商",
    ne_name OMC,
    data_type "数据类型",
    time_type "时间类型",
    src_file_path "文件",
    handle_state "结束状态",
    data_start_time "数据开始时间",
    data_end_time "数据结束时间",
    report_time "文件生成结束时间",
    TRUNC(src_file_lastmodifytime,'MI') "采集结束时间",
    TRUNC(start_time,'MI') "解析开始时间",
    TRUNC(end_time,'MI') "解析结束时间",
    succ_rows "成功行数",
    error_rows "失败行数",
    ROUND((end_time-data_end_time)*24*60) "数据总时延（分）",
    ROUND((report_time-data_end_time)*24*60) "文件生成时延（分）",
    ROUND((src_file_lastmodifytime-report_time)*24*60) "文件采集时延（分）",
    ROUND((end_time-src_file_lastmodifytime)*24*60) "文件解析时延（分）",
    ROUND((end_time-start_time)*24*60,2) "解析入库历时（分）",
    ROUND((usedtime_millis/1000/60),2) "解析入库CPU耗时（分）",
    error_message "错误信息"
    FROM NWOM.FPF_PARSER_MAIN
) t0
LEFT JOIN nwom.fpf_omc_standard t1
ON t0.omc=t1.omc AND t0."数据类型" = t1.data_type AND t0."时间类型"=t1.time_type
order by
    "域",
    "制式",
    "数据开始时间" DESC,
    "厂商" DESC,
    "数据类型",    
    "文件";

--时延统计-按OMC统计
CREATE OR REPLACE VIEW nwom.fpf_delay_stat_omc
AS
SELECT
    '北京' "地市",
    "域",
    "制式",
    "厂商",
    "数据类型",
    "时间类型",
    OMC,
    "数据开始时间",
    "数据结束时间",
    MAX("文件生成结束时间") "文件生成结束时间",
    MAX("采集结束时间") "采集结束时间",
    MAX("解析结束时间") "解析结束时间",
    (SELECT theoretic_file_count FROM nwom.fpf_omc_standard t1 WHERE t0.omc=t1
      .omc
      AND t0."数据类型" = t1.data_type
      AND t0."时间类型"=t1.time_type) "理论文件总数",
    COUNT(*) "实际文件总数",
    (SELECT theoretic_file_count FROM nwom.fpf_omc_standard t1 WHERE t0.omc=t1
      .omc
      AND t0."数据类型" = t1.data_type
      AND t0."时间类型"=t1.time_type)-COUNT(*) "缺失文件数",
    SUM(CASE "结束状态" WHEN '结束' THEN 1 ELSE 0 END) "正常文件数",
    SUM(CASE "结束状态" WHEN '出错' THEN 1 ELSE 0 END) "出错文件数",
    SUM(CASE "数据延误" WHEN '是' THEN 1 ELSE 0 END) "数据延误文件数",
    SUM(CASE "文件生成延误" WHEN '是' THEN 1 ELSE 0 END) "生成延误文件数",
    SUM(CASE "文件采集延误" WHEN '是' THEN 1 ELSE 0 END) "采集延误文件数",
    SUM(CASE "文件解析延误" WHEN '是' THEN 1 ELSE 0 END) "解析延误文件数",
    SUM("成功行数") "成功总行数",
    SUM("失败行数") "失败总行数",
    MAX("数据总时延（分）") "数据最大时延（分）",
    MAX("文件生成时延（分）") "生成最大时延（分）",
    MAX("文件采集时延（分）") "采集最大时延（分）",
    MAX("文件解析时延（分）") "解析最大时延（分）",
    ROUND((MAX("文件生成结束时间")-MIN("文件生成结束时间"))*24*60) "文件生成历时（分）",
    ROUND((MAX("采集结束时间")-MIN("采集结束时间"))*24*60) "文件采集历时（分）",
    ROUND((MAX("解析结束时间")-MIN("解析开始时间"))*24*60) "文件解析历时（分）",
    ROUND(SUM("解析入库CPU耗时（分）"),4) "解析CUP总耗时（分）",
    ROUND(AVG("解析入库CPU耗时（分）"),4) "平均解析CPU耗时（分）"
FROM
    nwom.fpf_delay_detail t0
GROUP BY
    "域",
    "制式",
    "厂商",
    OMC,
    "数据类型",
    "时间类型",
    "数据开始时间",
    "数据结束时间"
ORDER BY
    "域",
    "制式",
    "数据开始时间" DESC,
    "厂商" DESC,
    "数据类型";

--时延统计-按厂商统计
CREATE OR REPLACE VIEW nwom.fpf_delay_stat_vendor
AS
SELECT
    '北京' "地市",
    "域",
    "制式",
    "厂商",
    "数据类型",
    "时间类型",
    "数据开始时间",
    "数据结束时间",
    MAX("文件生成结束时间") "文件生成结束时间",
    MAX("采集结束时间") "采集结束时间",
    MAX("解析结束时间") "解析结束时间",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "制式"=t1.technology
      AND t0."厂商"=t1.vendor
      AND t0."数据类型" = t1.data_type
      AND t0."时间类型"=t1.time_type) "理论文件总数",
    COUNT(*) "实际文件总数",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "制式"=t1.technology
      AND t0."厂商"=t1.vendor
      AND t0."数据类型" = t1.data_type
      AND t0."时间类型"=t1.time_type)-COUNT(*) "缺失文件数",
    SUM(CASE "结束状态" WHEN '结束' THEN 1 ELSE 0 END) "正常文件数",
    SUM(CASE "结束状态" WHEN '出错' THEN 1 ELSE 0 END) "出错文件数",
    SUM(CASE "数据延误" WHEN '是' THEN 1 ELSE 0 END) "数据延误文件数",
    SUM(CASE "文件生成延误" WHEN '是' THEN 1 ELSE 0 END) "生成延误文件数",
    SUM(CASE "文件采集延误" WHEN '是' THEN 1 ELSE 0 END) "采集延误文件数",
    SUM(CASE "文件解析延误" WHEN '是' THEN 1 ELSE 0 END) "解析延误文件数",
    SUM("成功行数") "成功总行数",
    SUM("失败行数") "失败总行数",
    MAX("数据总时延（分）") "数据最大时延（分）",
    MAX("文件生成时延（分）") "生成最大时延（分）",
    MAX("文件采集时延（分）") "采集最大时延（分）",
    MAX("文件解析时延（分）") "解析最大时延（分）",
    ROUND((MAX("文件生成结束时间")-MIN("文件生成结束时间"))*24*60) "文件生成历时（分）",
    ROUND((MAX("采集结束时间")-MIN("采集结束时间"))*24*60) "文件采集历时（分）",
    ROUND((MAX("解析结束时间")-MIN("解析开始时间"))*24*60) "文件解析历时（分）",
    ROUND(SUM("解析入库CPU耗时（分）"),4) "解析CUP总耗时（分）",
    ROUND(AVG("解析入库CPU耗时（分）"),4) "平均解析CPU耗时（分）"
FROM
    nwom.fpf_delay_detail t0
GROUP BY
    "域",
    "制式",
    "厂商",
    "数据类型",
    "时间类型",
    "数据开始时间",
    "数据结束时间"
ORDER BY
    "域",
    "制式",
    "数据开始时间" DESC,
    "厂商" DESC,
    "数据类型" DESC;

--时延统计-按制式统计
CREATE OR REPLACE VIEW nwom.fpf_delay_stat_tech
AS
SELECT
    '北京' "地市",
    "域",
    "制式",
    "数据类型",
    "时间类型",
    "数据开始时间",
    "数据结束时间",
    MAX("文件生成结束时间") "文件生成结束时间",
    MAX("采集结束时间") "采集结束时间",
    MAX("解析结束时间") "解析结束时间",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "制式"=t1.technology
      AND t0."数据类型" = t1.data_type
      AND t0."时间类型"=t1.time_type) "理论文件总数",
    COUNT(*) "实际文件总数",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "制式"=t1.technology
      AND t0."数据类型" = t1.data_type
      AND t0."时间类型"=t1.time_type)-COUNT(*) "缺失文件数",
    SUM(CASE "结束状态" WHEN '结束' THEN 1 ELSE 0 END) "正常文件数",
    SUM(CASE "结束状态" WHEN '出错' THEN 1 ELSE 0 END) "出错文件数",
    SUM(CASE "数据延误" WHEN '是' THEN 1 ELSE 0 END) "数据延误文件数",
    SUM(CASE "文件生成延误" WHEN '是' THEN 1 ELSE 0 END) "生成延误文件数",
    SUM(CASE "文件采集延误" WHEN '是' THEN 1 ELSE 0 END) "采集延误文件数",
    SUM(CASE "文件解析延误" WHEN '是' THEN 1 ELSE 0 END) "解析延误文件数",
    SUM("成功行数") "成功总行数",
    SUM("失败行数") "失败总行数",
    MAX("数据总时延（分）") "数据最大时延（分）",
    MAX("文件生成时延（分）") "生成最大时延（分）",
    MAX("文件采集时延（分）") "采集最大时延（分）",
    MAX("文件解析时延（分）") "解析最大时延（分）",
    ROUND((MAX("文件生成结束时间")-MIN("文件生成结束时间"))*24*60) "文件生成历时（分）",
    ROUND((MAX("采集结束时间")-MIN("采集结束时间"))*24*60) "文件采集历时（分）",
    ROUND((MAX("解析结束时间")-MIN("解析开始时间"))*24*60) "文件解析历时（分）",
    ROUND(SUM("解析入库CPU耗时（分）"),4) "解析CUP总耗时（分）",
    ROUND(AVG("解析入库CPU耗时（分）"),4) "平均解析CPU耗时（分）"
FROM
    nwom.fpf_delay_detail t0
GROUP BY
    "域",
    "制式",
    "数据类型",
    "时间类型",
    "数据开始时间",
    "数据结束时间"
ORDER BY
    "域",
    "制式",
    "数据开始时间" DESC,
    "数据类型"  DESC;