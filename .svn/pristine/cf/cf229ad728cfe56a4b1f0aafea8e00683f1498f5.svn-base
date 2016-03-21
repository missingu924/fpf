alter session set nls_date_format='yyyy-mm-dd hh24:mi:ss'

select table_name,count(*) from nwom.fpf_log_sqlldr group by table_name

select trunc(start_time,'MI'),handle_state,count(*) from nwom.fpf_log_parser 
where handle_state='结束'
group by trunc(start_time,'MI'),handle_state
order by 1 desc

select trunc(start_time,'HH24'),handle_state,count(*) from nwom.fpf_log_parser 
where handle_state='结束'
group by trunc(start_time,'HH24'),handle_state
order by 1 desc

select * from cm.f_t_c_rnc

select * from pm.f_t_c_rnc_q_test 

select start_time,count(*) from pm.f_t_c_rnc_q_test 
where start_time>='2012-12-26'
group by start_time
order by 1 

select vendor_uk,start_time,count(*) from pm.f_t_c_rnc_q_test 
where start_time='2012-12-26 5:45'
group by vendor_uk,start_time
order by 1 

select vendor_uk,trunc(start_time,'HH24'),count(*) from pm.f_t_c_rnc_q_test 
where start_time>='2012-12-26'
group by vendor_uk,trunc(start_time,'HH24')
order by 1 ,2


select * from nwom.fpf_ftp_connect where ftp_server_name='NSN-OMC1'

select * from nwom.fpf_ftp_connect-- where ftp_server_name='PT-OMC1'

select ftp_server_name,start_time,round(usedtime_millis/1000/60,1),ftp_dl_files_count from nwom.fpf_ftp_nestdl  where ftp_server_name='ZTE-OMC3' 
order by ftp_server_name,start_time desc

select * from nwom.fpf_ftp_nestdl  where ftp_server_name='ZTE-OMC3' 
order by ftp_server_name,start_time desc

select * from nwom.fpf_ftp_onefiledl where --dest_file_path like '%NSN-OMC1%' and 
start_time>'2012-12-26 21:0:0'

select ftp_server_name,trunc(start_time,'HH24'),count(*) from nwom.fpf_ftp_onefiledl 
where start_time>'2012-12-26 21:0:0'
group by  ftp_server_name,trunc(start_time,'HH24')
order by 1 desc,2 desc
 
 
select ftp_server_name,substr(src_file_path,7,12),min(end_time),max(end_time),count(*) from nwom.fpf_ftp_onefiledl 
where start_time>='2012-12-26 14:0:0' and ftp_server_name like '%ZTE-OMC3%'
group by  ftp_server_name,trunc(start_time,'HH24'),substr(src_file_path,7,12)
order by 1,3 desc

select ftp_server_name,src_file_lastmodifytime,end_time,end_time-src_file_lastmodifytime from nwom.fpf_ftp_onefiledl


--各厂商按小时按omc采集文件个数统计分析
select 
ftp_server_name,
trunc(start_time,'HH24') "时段",
count(*) "文件数" 
from nwom.fpf_ftp_onefiledl
where  start_time>='2012-12-26 10:0:0'
group by 
trunc(start_time,'HH24'),
ftp_server_name
order by
1 desc,2 desc

--ZTE：按指标时段按omc统计文件个数
select 
ftp_server_name,
'15分钟' "上报周期",
to_date(substr(src_file_path,27,13),'yyyymmdd.hh24mi') "指标开始时间",
min(start_time) "采集开始时间",
max(end_time) "采集结束时间",
count(*) "文件数" ,
round((max(end_time)-min(end_time))*24*60,2) "总耗时(分)",
round(sum(usedtime_millis)/1000/60,1) "纯下载耗时(分)"
from nwom.fpf_ftp_onefiledl
where  start_time>='2012-12-26 21:45:0' and ftp_server_name like '%ZTE%' and handle_state='结束'
group by 
to_date(substr(src_file_path,27,13),'yyyymmdd.hh24mi'),
ftp_server_name
--order by
--1 desc,3 desc
union
--NSN：按指标时段按omc统计文件个数
select 
ftp_server_name,
'15分钟' "上报周期",
to_date(substr(src_file_path,70,13),'yyyymmdd.hh24mi') "指标开始时间",
min(start_time) "采集开始时间",
max(end_time) "采集结束时间",
count(*) "文件数" ,
round((max(end_time)-min(end_time))*24*60,2) "总耗时(分)",
round(sum(usedtime_millis)/1000/60,1) "纯下载耗时(分)"
from nwom.fpf_ftp_onefiledl
where  start_time>='2012-12-26 21:45:0' and ftp_server_name like '%NSN%' and handle_state='结束'
group by 
to_date(substr(src_file_path,70,13),'yyyymmdd.hh24mi'),
ftp_server_name
--order by
--1 desc,3 desc
union
--PT：按指标时段按omc统计文件个数
select 
ftp_server_name,
'15分钟' "上报周期",
to_date(substr(src_file_path,49,13),'yyyymmdd.hh24mi') "指标开始时间",
min(start_time) "采集开始时间",
max(end_time) "采集结束时间",
count(*) "文件数" ,
round((max(end_time)-min(end_time))*24*60,2) "总耗时(分)",
round(sum(usedtime_millis)/1000/60,1) "纯下载耗时(分)"
from nwom.fpf_ftp_onefiledl
where  start_time>='2012-12-26 21:45:0' and ftp_server_name like '%PT%' and handle_state='结束'
group by 
to_date(substr(src_file_path,49,13),'yyyymmdd.hh24mi'),
ftp_server_name
order by
1 desc,3 desc




select * from nwom.fpf_log_parser where handle_state='出错' and src_file_path not like '%ManagedElement%' order by start_time desc

select * from nwom.fpf_log_sqlldr where handle_state='出错' and src_file_path not like '%ManagedElement%' order by start_time desc

select src_file_path,count(*) from nwom.fpf_ftp_onefiledl where --handle_state='出错'
src_file_path like '%24A20121226.2230%' and ftp_server_name='ZTE-OMC3'
group by src_file_path
order by 2 desc

select * from nwom.fpf_ftp_onefiledl where src_file_path='/pm/PM201212262256+080024A20121226.2230+0800-20121226.2245+0800_390_Carrier_-_1.xml.gz'

select to_char(sysdate,'yyyymmdd.hh24miss') from dual

select to_date('20121227.102126','yyyymmdd.hh24miss') from dual

select * from nwom.fpf_ftp_onefiledl
where  start_time>='2012-12-26 21:45:0' and ftp_server_name like '%PT%' and handle_state='结束'


--TDPM文件采集解析入库延时分析详情
create or replace view nwom.fpf_delay_detail as 
select 
t0.ftp_server_name "FTP服务器",
t0.dest_file_path "数据文件", 
t1."指标开始时间",
t1."指标结束时间",
t0."文件生成时间",
round((t0."文件生成时间"-t1."指标开始时间")*24*60-15,1) "文件生成时延(分)",
t0."采集开始时间",
t0."采集结束时间",
round((t0."采集开始时间"-t0."文件生成时间")*24*60,1) "文件采集时延(分)",
t1."解析开始时间",
t1."解析结束时间",
round((t1."解析结束时间"-t0."采集结束时间")*24*60,1) "文件解析时延(分)",
round((t1."解析结束时间"-t0."文件生成时间")*24*60,1) "网优平台总时延(分)",
round((t1."解析结束时间"-t1."指标开始时间")*24*60-15,1) "全过程总时延(分)",
t1.handle_state "解析入库结果"
from 
(
    select 
    ftp_server_name,
    dest_file_path,
    src_file_lastmodifytime "文件生成时间",
    start_time "采集开始时间",
    end_time "采集结束时间"
    from nwom.fpf_ftp_onefiledl where src_file_path not like '%ManagedElement%'
) t0
full join 
(
    select 
    src_file_path,
    handle_state,
    data_start_time "指标开始时间",
    data_end_time "指标结束时间",
    start_time "解析开始时间",
    end_time "解析结束时间" 
    from nwom.fpf_parser_main
) t1
on t0.dest_file_path=t1.src_file_path

--TDPM文件采集分析入库时延分析统计
create or replace view nwom.fpf_delay_stat as
select 
"FTP服务器",
count(*) "实际文件数",
'' "理论文件数",
sum(decode("解析入库结果",'结束',1,0)) "处理成功文件数",
sum(decode("解析入库结果",'出错',1,0)) "处理出错文件数",
"指标开始时间",
"指标结束时间",
min("文件生成时间") "文件生成开始时间",
max("文件生成时间") "文件生成结束时间",
round(avg("文件生成时延(分)"),1) "平均文件生成时延(分)",
round(min("文件生成时延(分)"),1) "最小文件生成时延(分)",
round(max("文件生成时延(分)"),1) "最大文件生成时延(分)",
round((max("文件生成时间")-min("文件生成时间"))*24*60,1) "文件生成耗时(分)",
min("采集开始时间") "采集开始时间",
max("采集结束时间") "采集结束时间",
round(avg("文件采集时延(分)"),1) "平均文件采集时延(分)",
round(min("文件采集时延(分)"),1) "最小文件采集时延(分)",
round(max("文件采集时延(分)"),1) "最大文件采集时延(分)",
round((max("采集结束时间")-min("采集开始时间"))*24*60,1) "文件采集耗时(分)",
min("解析开始时间") "解析开始时间",
max("解析结束时间") "解析结束时间",
round(avg("文件解析时延(分)"),1) "平均文件解析时延(分)",
round(min("文件解析时延(分)"),1) "最小文件解析时延(分)",
round(max("文件解析时延(分)"),1) "最大文件解析时延(分)",
round((max("解析结束时间")-min("解析开始时间"))*24*60,1) "文件解析耗时(分)",
round(avg("网优平台总时延(分)"),1) "网优平台平均时延(分)",
round(min("网优平台总时延(分)"),1) "网优平台最小时延(分)",
round(max("网优平台总时延(分)"),1) "网优平台最大时延(分)",
round(avg("全过程总时延(分)"),1) "全过程平均时延(分)",
round(min("全过程总时延(分)"),1) "全过程最小时延(分)",
round(max("全过程总时延(分)"),1) "全过程最大时延(分)"
from nwom.fpf_delay_detail 
--where "采集开始时间" >'2012-12-26 12:0:0'
group by "FTP服务器","指标开始时间","指标结束时间"
order by "指标开始时间" desc,"FTP服务器" desc

select * from nwom.fpf_tdpm_delay_detail
order by "指标开始时间" desc,"FTP服务器" desc

select * from nwom.fpf_tdpm_delay_stat --where "解析开始时间">='2012-12-27 0:0:0' and "解析开始时间"<'2012-12-27 12:0:0' 
order by "指标开始时间" desc,"FTP服务器" desc

select * from nwom.fpf_log_parser where src_file_path like '%PM201212271449+080024A20121227.1430+0800-1445+0800_41_RNC287UtranCell_-_1.xml%'

select * from nwom.fpf_ftp_connect where start_time>='2012-12-27 0:0:0' and start_time<'2012-12-27 12:0:0' 