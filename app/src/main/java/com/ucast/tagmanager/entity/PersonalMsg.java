package com.ucast.tagmanager.entity;

/**
 * Created by Allen on 2017/6/13.
 */

public class PersonalMsg {
    private String login_id;
    private String password;
    private String emp_name;
    private String company_name;
    private String group_id;
    private String role;
    private String emp_phonenumber;
    private String emp_emial;
    private String create_date;
    private String work_state;

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmp_phonenumber() {
        return emp_phonenumber;
    }

    public void setEmp_phonenumber(String emp_phonenumber) {
        this.emp_phonenumber = emp_phonenumber;
    }

    public String getEmp_emial() {
        return emp_emial;
    }

    public void setEmp_emial(String emp_emial) {
        this.emp_emial = emp_emial;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getWork_state() {
        return work_state;
    }

    public void setWork_state(String work_state) {
//        if (work_state.equals("0")) {
//            this.work_state = ExceptionApplication.getInstance().getString(R.string.no);
//        }else{
//            this.work_state=ExceptionApplication.getInstance().getString(R.string.yes);
//        }
        this.work_state=work_state;
    }
}
