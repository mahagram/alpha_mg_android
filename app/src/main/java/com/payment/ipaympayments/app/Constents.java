package com.payment.ipaympayments.app;

import com.payment.ipaympayments.bppsServices.model.BillPayModel;

import java.text.SimpleDateFormat;

public class Constents {

    public static final double MILLISECOND_CONVERTER = 2.777777777777778e-7; //To conver miliseconds in to hourse
    public static final String TRUE = "true";
    public static final int IMAGE_HIEGHT = 400;
    public static final int IMAGE_WIDTH = 400;

    public static final int MIN_MEMORY_FOR_IMAGE_CAPTURE = 50;
    public static final int MIN_MEMORY_FOR_APP = 150;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat SHOWING_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat COMMON_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("MMMM");

    public static class URL {
        public static String baseUrl = "https://alphamg.ipaymaeps.com/";
    }

    public static String isBack = "1";
    public static String MOBILE_ID;
    public static int GLOBAL_POSITION = -1;
    public static BillPayModel BILL_MODEL;
}
