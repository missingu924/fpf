package com.inspur.meta.fpf.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * FPF的RMI接口
 */
public interface IFpfRmi extends Remote {
	public static final String RMI_NAME = "IQUALITYMANAGER";

	// 以下为添加、修改、删除任务接口，ftp上传后通知下刚才的操作
	/**
	 * 添加任务
	 * @param 发布的任务ID
	 * @return 执行结果
	 */
	public String addTask(String publishtTaskId) throws RemoteException;
	/**
	 * 修改任务
	 * @param 发布的任务ID
	 * @return 执行结果
	 */
	public String updateTask(String publishtTaskId) throws RemoteException;
	/**
	 * 删除任务
	 * @param 发布的任务ID
	 * @return 执行结果
	 */
	public String deleteTask(String publishtTaskId) throws RemoteException;
	// 以下为测试任务或stage用接口
	/**
	 * 运行发布后的任务（此时，xml文件已经上传了，只传发布的任务ID即可）
	 * 
	 * @param publishtTaskId 任务ID
	 * @param publishTaskName 任务名称
	 * @param varMap 外部参数，预留
	 * @return 执行结果
	 */
	public String runTask(String publishtTaskId, String publishTaskName, Map<String, String> varMap) throws RemoteException;

	/**
	 * 测试未发布的任务（此时，只传入xml文件对应的字符串，不做文件上传）
	 * 
	 * @param publishTaskName 任务名称
	 * @param varMap 外部参数，预留
	 * @param xmlMap xml文件对应的字符串，key：配置文件的文件名，value：配置文件的内容字符串
	 * @return 执行结果
	 */
	public String testTask(String publishTaskName, Map<String, String> varMap, Map<String, String> xmlMap) throws RemoteException;

	/**
	 * 测试stage
	 * 
	 * @param varMap 外部参数，预留
	 * @param xmlMap xml文件对应的字符串，key：配置文件的文件名，value：配置文件的内容字符串
	 * @return 执行结果
	 */
	public String testStage(Map<String, String> varMap, Map<String, String> xmlMap) throws RemoteException;

}