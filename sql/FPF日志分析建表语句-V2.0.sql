/* Formatted on 2013/1/4 13:30:42 (QP5 v5.114.809.3010) */
DROP TABLE NWOM.FPF_PARSER_MAIN;
DROP TABLE NWOM.FPF_PARSER_MIDDLE;
DROP TABLE NWOM.FPF_PARSER_SQLLDR;
DROP TABLE NWOM.FPF_FTP_CONNECT;
DROP TABLE NWOM.FPF_FTP_NESTDL;
DROP TABLE NWOM.FPF_FTP_ONEFILEDL;

CREATE TABLE NWOM.FPF_PARSER_MAIN
(
   CITY_NAME                 VARCHAR (128) DEFAULT '����',
   DOMAIN                    VARCHAR (128),                          --��/רҵ
   TECHNOLOGY                VARCHAR (128),                             --��ʽ
   VENDOR                    VARCHAR (128),                             --����
   DATA_TYPE                 VARCHAR (128),                         --��������
   BUSINESS_TYPE             VARCHAR (128),                         --ҵ������
   NE_TYPE                   VARCHAR (128),                     --�ϱ���Ԫ����
   TIME_TYPE                 VARCHAR (128),                     --�ϱ�ʱ������
   NE_NAME                   VARCHAR (128),                         --��Ԫ����
   REPORT_TIME               DATE,                              --�ļ�����ʱ��
   DATA_START_TIME           DATE,                              --���ݿ�ʼʱ��
   DATA_END_TIME             DATE,                              --���ݽ���ʱ��
   HANDLE_NAME               VARCHAR (256),                         --������
   HANDLE_STATE              VARCHAR (128),                         --����״̬
   SRC_FILE_PATH             VARCHAR (1024),                          --Դ�ļ�
   SRC_FILE_LENGTH           NUMBER (32),                   --Դ�ļ���С(Byte)
   SRC_FILE_LASTMODIFYTIME   DATE,                        --Դ�ļ�����޸�ʱ��
   START_TIME                DATE,                          --���δ���ʼʱ��
   END_TIME                  DATE,                          --���δ������ʱ��
   USEDTIME_MILLIS           NUMBER (32),                 --���δ����ʱ(����)
   ERROR_MESSAGE             VARCHAR (4000),                --���δ��������Ϣ
   SUCC_ROWS                 NUMBER (32),                           --�ɹ�����
   ERROR_ROWS                NUMBER (32),                           --ʧ������
   TIME_STAMP                DATE DEFAULT SYSDATE                     --ʱ���
);

CREATE INDEX NWOM.FPF_PARSER_MAIN_idx00 ON NWOM.FPF_PARSER_MAIN (DATA_START_TIME)L;

CREATE INDEX NWOM.FPF_PARSER_MAIN_IDX01 ON NWOM.FPF_PARSER_MAIN (SRC_FILE_PATH);

CREATE INDEX NWOM.FPF_PARSER_MAIN_IDX02 ON NWOM.FPF_PARSER_MAIN (START_TIME);

CREATE TABLE NWOM.FPF_PARSER_MIDDLE
(
   CITY_NAME                 				VARCHAR (128) DEFAULT '����',
   DOMAIN                           VARCHAR (128),                   --��/רҵ
   TECHNOLOGY                       VARCHAR (128),                      --��ʽ
   VENDOR                           VARCHAR (128),                      --����
   DATA_TYPE                        VARCHAR (128),                  --��������
   BUSINESS_TYPE                    VARCHAR (128),                  --ҵ������
   NE_TYPE                          VARCHAR (128),              --�ϱ���Ԫ����
   TIME_TYPE                        VARCHAR (128),              --�ϱ�ʱ������
   NE_NAME                          VARCHAR (128),                  --��Ԫ����
   REPORT_TIME                      DATE,                       --�ļ�����ʱ��
   DATA_START_TIME                  DATE,                       --���ݿ�ʼʱ��
   DATA_END_TIME                    DATE,                       --���ݽ���ʱ��
   HANDLE_NAME                      VARCHAR (256),                  --������
   HANDLE_STATE                     VARCHAR (128),                  --����״̬
   SRC_FILE_PATH                    VARCHAR (1024),                   --Դ�ļ�
   SRC_FILE_LENGTH                  NUMBER (32),            --Դ�ļ���С(Byte)
   SRC_FILE_LASTMODIFYTIME          DATE,                 --Դ�ļ�����޸�ʱ��
   START_TIME                       DATE,                   --���δ���ʼʱ��
   END_TIME                         DATE,                   --���δ������ʱ��
   USEDTIME_MILLIS                  NUMBER (32),          --���δ����ʱ(����)
   ERROR_MESSAGE                    VARCHAR (4000),         --���δ��������Ϣ
   NEEDHANDLE_FILE_PATH             VARCHAR (1024),               --�������ļ�
   NEEDHANDLE_FILE_LENGTH           NUMBER (32),        --�������ļ���С(Byte)
   NEEDHANDLE_FILE_LASTMODIFYTIME   DATE,             --�������ļ�����޸�ʱ��
   HANDLED_FILE_PATH                VARCHAR (1024),               --������ļ�
   HANDLED_FILE_LENGTH              NUMBER (32),        --������ļ���С(Byte)
   HANDLED_FILE_LASTMODIFYTIME      DATE,             --������ļ�����޸�ʱ��
   TIME_STAMP                       DATE DEFAULT SYSDATE              --ʱ���
);

CREATE INDEX NWOM.FPF_PARSER_MIDDLE_IDX01 ON NWOM.FPF_PARSER_MIDDLE (SRC_FILE_PATH);

CREATE INDEX NWOM.FPF_PARSER_MIDDLE_IDX02 ON NWOM.FPF_PARSER_MIDDLE (START_TIME);

CREATE TABLE NWOM.FPF_PARSER_SQLLDR
(
   CITY_NAME                  VARCHAR (128) DEFAULT '����',
   DOMAIN                     VARCHAR (128),                         --��/רҵ
   TECHNOLOGY                 VARCHAR (128),                            --��ʽ
   VENDOR                     VARCHAR (128),                            --����
   DATA_TYPE                  VARCHAR (128),                        --��������
   BUSINESS_TYPE              VARCHAR (128),                        --ҵ������
   NE_TYPE                    VARCHAR (128),                    --�ϱ���Ԫ����
   TIME_TYPE                  VARCHAR (128),                    --�ϱ�ʱ������
   NE_NAME                    VARCHAR (128),                        --��Ԫ����
   REPORT_TIME                DATE,                             --�ļ�����ʱ��
   DATA_START_TIME            DATE,                             --���ݿ�ʼʱ��
   DATA_END_TIME              DATE,                             --���ݽ���ʱ��
   HANDLE_NAME                VARCHAR (256),                        --������
   HANDLE_STATE               VARCHAR (128),                        --����״̬
   SRC_FILE_PATH              VARCHAR (1024),                         --Դ�ļ�
   SRC_FILE_LENGTH            NUMBER (32),                  --Դ�ļ���С(Byte)
   SRC_FILE_LASTMODIFYTIME    DATE,                       --Դ�ļ�����޸�ʱ��
   START_TIME                 DATE,                         --���δ���ʼʱ��
   END_TIME                   DATE,                         --���δ������ʱ��
   USEDTIME_MILLIS            NUMBER (32),                --���δ����ʱ(����)
   ERROR_MESSAGE              VARCHAR (4000),               --���δ��������Ϣ
   TABLE_NAME                 VARCHAR (128),                        --��Ӧ����
   DATA_FILE_PATH             VARCHAR (1024),                     --������ļ�
   DATA_FILE_LENGTH           NUMBER (32),              --������ļ���С(Byte)
   DATA_FILE_LASTMODIFYTIME   DATE,                   --������ļ�����޸�ʱ��
   SUCC_ROWS                  NUMBER (32),                          --�ɹ�����
   ERROR_ROWS                 NUMBER (32),                          --ʧ������
   TIME_STAMP                 DATE DEFAULT SYSDATE                   --ʱ���
);

CREATE INDEX NWOM.FPF_PARSER_SQLLDR_IDX01 ON NWOM.FPF_PARSER_SQLLDR (SRC_FILE_PATH, DATA_FILE_PATH);

CREATE INDEX NWOM.FPF_PARSER_SQLLDR_IDX02 ON NWOM.FPF_PARSER_SQLLDR (START_TIME);

CREATE TABLE NWOM.FPF_FTP_CONNECT
(
   DOMAIN            VARCHAR (128),                                  --��/רҵ
   TECHNOLOGY        VARCHAR (128),                                     --��ʽ
   VENDOR            VARCHAR (128),                                     --����
   DATA_TYPE         VARCHAR (128),                                 --��������
   BUSINESS_TYPE     VARCHAR (128),                                 --ҵ������
   NE_TYPE           VARCHAR (128),                             --�ϱ���Ԫ����
   TIME_TYPE         VARCHAR (128),                             --�ϱ�ʱ������
   NE_NAME           VARCHAR (128),                                 --��Ԫ����
   REPORT_TIME       DATE,                                      --�ļ�����ʱ��
   HANDLE_NAME       VARCHAR (256),                                 --������
   HANDLE_STATE      VARCHAR (128),                                 --����״̬
   FTP_SERVER_NAME   VARCHAR (1024),                             --FTP��������
   START_TIME        DATE,                                  --���δ���ʼʱ��
   END_TIME          DATE,                                  --���δ������ʱ��
   ERROR_MESSAGE     VARCHAR (4000),                        --���δ��������Ϣ
   TIME_STAMP        DATE DEFAULT SYSDATE                             --ʱ���
);

CREATE INDEX NWOM.FPF_FTP_CONNECT_IDX01 ON NWOM.FPF_FTP_CONNECT (START_TIME, FTP_SERVER_NAME);

CREATE TABLE NWOM.FPF_FTP_NESTDL
(
   DOMAIN               VARCHAR (128),                               --��/רҵ
   TECHNOLOGY           VARCHAR (128),                                  --��ʽ
   VENDOR               VARCHAR (128),                                  --����
   DATA_TYPE            VARCHAR (128),                              --��������
   BUSINESS_TYPE        VARCHAR (128),                              --ҵ������
   NE_TYPE              VARCHAR (128),                          --�ϱ���Ԫ����
   TIME_TYPE            VARCHAR (128),                          --�ϱ�ʱ������
   NE_NAME              VARCHAR (128),                              --��Ԫ����
   REPORT_TIME          DATE,                                   --�ļ�����ʱ��
   HANDLE_NAME          VARCHAR (256),                              --������
   HANDLE_STATE         VARCHAR (128),                              --����״̬
   FTP_SERVER_NAME      VARCHAR (1024),                          --FTP��������
   FTP_DIR              VARCHAR (1024),                              --FTPĿ¼
   FTP_FILTER           VARCHAR (1024),                          --FTP��������
   FTP_DL_FILES_COUNT   NUMBER (32),                          --���������ļ���
   START_TIME           DATE,                               --���δ���ʼʱ��
   END_TIME             DATE,                               --���δ������ʱ��
   USEDTIME_MILLIS      NUMBER (32),                      --���δ����ʱ(����)
   ERROR_MESSAGE        VARCHAR (4000),                     --���δ��������Ϣ
   TIME_STAMP           DATE DEFAULT SYSDATE                          --ʱ���
);

CREATE INDEX NWOM.FPF_FTP_NESTDL_IDX01 ON NWOM.FPF_FTP_NESTDL (START_TIME, FTP_SERVER_NAME);

CREATE TABLE NWOM.FPF_FTP_ONEFILEDL
(
   DOMAIN                     VARCHAR (128),                         --��/רҵ
   TECHNOLOGY                 VARCHAR (128),                            --��ʽ
   VENDOR                     VARCHAR (128),                            --����
   DATA_TYPE                  VARCHAR (128),                        --��������
   BUSINESS_TYPE              VARCHAR (128),                        --ҵ������
   NE_TYPE                    VARCHAR (128),                    --�ϱ���Ԫ����
   TIME_TYPE                  VARCHAR (128),                    --�ϱ�ʱ������
   NE_NAME                    VARCHAR (128),                        --��Ԫ����
   REPORT_TIME                DATE,                             --�ļ�����ʱ��
   DATA_START_TIME            DATE,                             --���ݿ�ʼʱ��
   DATA_END_TIME              DATE,                             --���ݽ���ʱ��
   HANDLE_NAME                VARCHAR (256),                        --������
   HANDLE_STATE               VARCHAR (128),                        --����״̬
   FTP_SERVER_NAME            VARCHAR (256),                     --FTP��������
   SRC_FILE_PATH              VARCHAR (1024),                         --Դ�ļ�
   SRC_FILE_LENGTH            NUMBER (32),                  --Դ�ļ���С(Byte)
   SRC_FILE_LASTMODIFYTIME    DATE,                       --Դ�ļ�����޸�ʱ��
   START_TIME                 DATE,                         --���δ���ʼʱ��
   END_TIME                   DATE,                         --���δ������ʱ��
   USEDTIME_MILLIS            NUMBER (32),                --���δ����ʱ(����)
   ERROR_MESSAGE              VARCHAR (4000),               --���δ��������Ϣ
   DEST_FILE_PATH             VARCHAR (1024),                       --Ŀ���ļ�
   DEST_FILE_LENGTH           NUMBER (32),                --Ŀ���ļ���С(Byte)
   DEST_FILE_LASTMODIFYTIME   DATE,                     --Ŀ���ļ�����޸�ʱ��
   TIME_STAMP                 DATE DEFAULT SYSDATE                    --ʱ���
);

CREATE INDEX NWOM.FPF_FTP_ONEFILEDL_IDX01 ON NWOM.FPF_FTP_ONEFILEDL (START_TIME, FTP_SERVER_NAME, SRC_FILE_PATH, DEST_FILE_PATH);
                              
CREATE TABLE NWOM.FPF_OMC_STANDARD
(
   DOMAIN                    VARCHAR (128),                          --��/רҵ
   TECHNOLOGY                VARCHAR (128),                             --��ʽ
   VENDOR                    VARCHAR (128),                             --����
   DATA_TYPE                 VARCHAR (128),                         --��������
   TIME_TYPE                 VARCHAR (128),                     --�ϱ�ʱ������
   OMC                       VARCHAR (128),                              --OMC
   RNC_BSC_COUNT             INT,                               --RNC��BSC����
   THEORETIC_FILE_COUNT      NUMBER (32),                         --�����ļ���
   THEORETIC_ROWS            NUMBER (32),                       --������������
   ALLOWED_MAX_FILE_DELAY    INT,               --���������ļ�����ʱ��(����)
   ALLOWED_MAX_DL_DELAY      INT,                   --��������ɼ�ʱ��(����)
   ALLOWED_MAX_PARSE_DELAY   INT,                   --�����������ʱ��(����)
   ALLOWED_MAX_DATA_DELAY    INT                  --��������������ʱ��(����)
);
                             




--ʱ��ͳ�ƣ����ļ�ʱ������
CREATE OR REPLACE VIEW nwom.fpf_delay_detail
AS
SELECT t0.*,
CASE WHEN ((t0."������ʱ�ӣ��֣�"-t1.ALLOWED_MAX_DATA_DELAY) >0) THEN '��' ELSE
      ''
      END AS "��������",
CASE WHEN ((t0."�ļ�����ʱ�ӣ��֣�"-t1.ALLOWED_MAX_FILE_DELAY) >0) THEN '��'
      ELSE ''
      END AS "�ļ���������",
CASE WHEN ((t0."�ļ��ɼ�ʱ�ӣ��֣�"-t1.ALLOWED_MAX_DL_DELAY) >0) THEN '��' ELSE
      ''
      END AS "�ļ��ɼ�����",
CASE WHEN ((t0."�ļ�����ʱ�ӣ��֣�"-t1.ALLOWED_MAX_PARSE_DELAY) >0) THEN '��'
      ELSE ''
      END AS "�ļ���������"
FROM
(
    SELECT
    '����' "����",
    domain "��",
    technology "��ʽ",
    vendor "����",
    ne_name OMC,
    data_type "��������",
    time_type "ʱ������",
    src_file_path "�ļ�",
    handle_state "����״̬",
    data_start_time "���ݿ�ʼʱ��",
    data_end_time "���ݽ���ʱ��",
    report_time "�ļ����ɽ���ʱ��",
    TRUNC(src_file_lastmodifytime,'MI') "�ɼ�����ʱ��",
    TRUNC(start_time,'MI') "������ʼʱ��",
    TRUNC(end_time,'MI') "��������ʱ��",
    succ_rows "�ɹ�����",
    error_rows "ʧ������",
    ROUND((end_time-data_end_time)*24*60) "������ʱ�ӣ��֣�",
    ROUND((report_time-data_end_time)*24*60) "�ļ�����ʱ�ӣ��֣�",
    ROUND((src_file_lastmodifytime-report_time)*24*60) "�ļ��ɼ�ʱ�ӣ��֣�",
    ROUND((end_time-src_file_lastmodifytime)*24*60) "�ļ�����ʱ�ӣ��֣�",
    ROUND((end_time-start_time)*24*60,2) "���������ʱ���֣�",
    ROUND((usedtime_millis/1000/60),2) "�������CPU��ʱ���֣�",
    error_message "������Ϣ"
    FROM NWOM.FPF_PARSER_MAIN
) t0
LEFT JOIN nwom.fpf_omc_standard t1
ON t0.omc=t1.omc AND t0."��������" = t1.data_type AND t0."ʱ������"=t1.time_type
order by
    "��",
    "��ʽ",
    "���ݿ�ʼʱ��" DESC,
    "����" DESC,
    "��������",    
    "�ļ�";

--ʱ��ͳ��-��OMCͳ��
CREATE OR REPLACE VIEW nwom.fpf_delay_stat_omc
AS
SELECT
    '����' "����",
    "��",
    "��ʽ",
    "����",
    "��������",
    "ʱ������",
    OMC,
    "���ݿ�ʼʱ��",
    "���ݽ���ʱ��",
    MAX("�ļ����ɽ���ʱ��") "�ļ����ɽ���ʱ��",
    MAX("�ɼ�����ʱ��") "�ɼ�����ʱ��",
    MAX("��������ʱ��") "��������ʱ��",
    (SELECT theoretic_file_count FROM nwom.fpf_omc_standard t1 WHERE t0.omc=t1
      .omc
      AND t0."��������" = t1.data_type
      AND t0."ʱ������"=t1.time_type) "�����ļ�����",
    COUNT(*) "ʵ���ļ�����",
    (SELECT theoretic_file_count FROM nwom.fpf_omc_standard t1 WHERE t0.omc=t1
      .omc
      AND t0."��������" = t1.data_type
      AND t0."ʱ������"=t1.time_type)-COUNT(*) "ȱʧ�ļ���",
    SUM(CASE "����״̬" WHEN '����' THEN 1 ELSE 0 END) "�����ļ���",
    SUM(CASE "����״̬" WHEN '����' THEN 1 ELSE 0 END) "�����ļ���",
    SUM(CASE "��������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM(CASE "�ļ���������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM(CASE "�ļ��ɼ�����" WHEN '��' THEN 1 ELSE 0 END) "�ɼ������ļ���",
    SUM(CASE "�ļ���������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM("�ɹ�����") "�ɹ�������",
    SUM("ʧ������") "ʧ��������",
    MAX("������ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    MAX("�ļ�����ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    MAX("�ļ��ɼ�ʱ�ӣ��֣�") "�ɼ����ʱ�ӣ��֣�",
    MAX("�ļ�����ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    ROUND((MAX("�ļ����ɽ���ʱ��")-MIN("�ļ����ɽ���ʱ��"))*24*60) "�ļ�������ʱ���֣�",
    ROUND((MAX("�ɼ�����ʱ��")-MIN("�ɼ�����ʱ��"))*24*60) "�ļ��ɼ���ʱ���֣�",
    ROUND((MAX("��������ʱ��")-MIN("������ʼʱ��"))*24*60) "�ļ�������ʱ���֣�",
    ROUND(SUM("�������CPU��ʱ���֣�"),4) "����CUP�ܺ�ʱ���֣�",
    ROUND(AVG("�������CPU��ʱ���֣�"),4) "ƽ������CPU��ʱ���֣�"
FROM
    nwom.fpf_delay_detail t0
GROUP BY
    "��",
    "��ʽ",
    "����",
    OMC,
    "��������",
    "ʱ������",
    "���ݿ�ʼʱ��",
    "���ݽ���ʱ��"
ORDER BY
    "��",
    "��ʽ",
    "���ݿ�ʼʱ��" DESC,
    "����" DESC,
    "��������";

--ʱ��ͳ��-������ͳ��
CREATE OR REPLACE VIEW nwom.fpf_delay_stat_vendor
AS
SELECT
    '����' "����",
    "��",
    "��ʽ",
    "����",
    "��������",
    "ʱ������",
    "���ݿ�ʼʱ��",
    "���ݽ���ʱ��",
    MAX("�ļ����ɽ���ʱ��") "�ļ����ɽ���ʱ��",
    MAX("�ɼ�����ʱ��") "�ɼ�����ʱ��",
    MAX("��������ʱ��") "��������ʱ��",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "��ʽ"=t1.technology
      AND t0."����"=t1.vendor
      AND t0."��������" = t1.data_type
      AND t0."ʱ������"=t1.time_type) "�����ļ�����",
    COUNT(*) "ʵ���ļ�����",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "��ʽ"=t1.technology
      AND t0."����"=t1.vendor
      AND t0."��������" = t1.data_type
      AND t0."ʱ������"=t1.time_type)-COUNT(*) "ȱʧ�ļ���",
    SUM(CASE "����״̬" WHEN '����' THEN 1 ELSE 0 END) "�����ļ���",
    SUM(CASE "����״̬" WHEN '����' THEN 1 ELSE 0 END) "�����ļ���",
    SUM(CASE "��������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM(CASE "�ļ���������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM(CASE "�ļ��ɼ�����" WHEN '��' THEN 1 ELSE 0 END) "�ɼ������ļ���",
    SUM(CASE "�ļ���������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM("�ɹ�����") "�ɹ�������",
    SUM("ʧ������") "ʧ��������",
    MAX("������ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    MAX("�ļ�����ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    MAX("�ļ��ɼ�ʱ�ӣ��֣�") "�ɼ����ʱ�ӣ��֣�",
    MAX("�ļ�����ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    ROUND((MAX("�ļ����ɽ���ʱ��")-MIN("�ļ����ɽ���ʱ��"))*24*60) "�ļ�������ʱ���֣�",
    ROUND((MAX("�ɼ�����ʱ��")-MIN("�ɼ�����ʱ��"))*24*60) "�ļ��ɼ���ʱ���֣�",
    ROUND((MAX("��������ʱ��")-MIN("������ʼʱ��"))*24*60) "�ļ�������ʱ���֣�",
    ROUND(SUM("�������CPU��ʱ���֣�"),4) "����CUP�ܺ�ʱ���֣�",
    ROUND(AVG("�������CPU��ʱ���֣�"),4) "ƽ������CPU��ʱ���֣�"
FROM
    nwom.fpf_delay_detail t0
GROUP BY
    "��",
    "��ʽ",
    "����",
    "��������",
    "ʱ������",
    "���ݿ�ʼʱ��",
    "���ݽ���ʱ��"
ORDER BY
    "��",
    "��ʽ",
    "���ݿ�ʼʱ��" DESC,
    "����" DESC,
    "��������" DESC;

--ʱ��ͳ��-����ʽͳ��
CREATE OR REPLACE VIEW nwom.fpf_delay_stat_tech
AS
SELECT
    '����' "����",
    "��",
    "��ʽ",
    "��������",
    "ʱ������",
    "���ݿ�ʼʱ��",
    "���ݽ���ʱ��",
    MAX("�ļ����ɽ���ʱ��") "�ļ����ɽ���ʱ��",
    MAX("�ɼ�����ʱ��") "�ɼ�����ʱ��",
    MAX("��������ʱ��") "��������ʱ��",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "��ʽ"=t1.technology
      AND t0."��������" = t1.data_type
      AND t0."ʱ������"=t1.time_type) "�����ļ�����",
    COUNT(*) "ʵ���ļ�����",
    (SELECT SUM(theoretic_file_count) FROM nwom.fpf_omc_standard t1 WHERE t0.
      "��ʽ"=t1.technology
      AND t0."��������" = t1.data_type
      AND t0."ʱ������"=t1.time_type)-COUNT(*) "ȱʧ�ļ���",
    SUM(CASE "����״̬" WHEN '����' THEN 1 ELSE 0 END) "�����ļ���",
    SUM(CASE "����״̬" WHEN '����' THEN 1 ELSE 0 END) "�����ļ���",
    SUM(CASE "��������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM(CASE "�ļ���������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM(CASE "�ļ��ɼ�����" WHEN '��' THEN 1 ELSE 0 END) "�ɼ������ļ���",
    SUM(CASE "�ļ���������" WHEN '��' THEN 1 ELSE 0 END) "���������ļ���",
    SUM("�ɹ�����") "�ɹ�������",
    SUM("ʧ������") "ʧ��������",
    MAX("������ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    MAX("�ļ�����ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    MAX("�ļ��ɼ�ʱ�ӣ��֣�") "�ɼ����ʱ�ӣ��֣�",
    MAX("�ļ�����ʱ�ӣ��֣�") "�������ʱ�ӣ��֣�",
    ROUND((MAX("�ļ����ɽ���ʱ��")-MIN("�ļ����ɽ���ʱ��"))*24*60) "�ļ�������ʱ���֣�",
    ROUND((MAX("�ɼ�����ʱ��")-MIN("�ɼ�����ʱ��"))*24*60) "�ļ��ɼ���ʱ���֣�",
    ROUND((MAX("��������ʱ��")-MIN("������ʼʱ��"))*24*60) "�ļ�������ʱ���֣�",
    ROUND(SUM("�������CPU��ʱ���֣�"),4) "����CUP�ܺ�ʱ���֣�",
    ROUND(AVG("�������CPU��ʱ���֣�"),4) "ƽ������CPU��ʱ���֣�"
FROM
    nwom.fpf_delay_detail t0
GROUP BY
    "��",
    "��ʽ",
    "��������",
    "ʱ������",
    "���ݿ�ʼʱ��",
    "���ݽ���ʱ��"
ORDER BY
    "��",
    "��ʽ",
    "���ݿ�ʼʱ��" DESC,
    "��������"  DESC;