package com.water.project.presenter.check;

public interface CheckPresenter {
    /**
     * 发送蓝牙命令
     * @param status
     */
    public void sendData(int status);

    public void showToast(String msg);

    public void showLoding(String msg);

    public void clearLoding();
}
