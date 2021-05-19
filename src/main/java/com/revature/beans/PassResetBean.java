package com.revature.beans;

public class PassResetBean {

	private String oldPass;
	private String newPass;
	private String confirm;

	public String getOldPass() {
		return oldPass;
	}

	public void setOldPass(String oldPass) {
		this.oldPass = oldPass;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	@Override
	public String toString() {
		return "PassResetBean [oldPass=" + oldPass + ", newPass=" + newPass + ", confirm=" + confirm + "]";
	}

}
