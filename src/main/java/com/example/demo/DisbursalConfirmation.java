//package com.example.demo;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.validation.Valid;
//import javax.validation.constraints.*;
//import java.text.SimpleDateFormat;
//import java.util.List;
//
//
//@Getter
//@Setter
//@ToString
//public class DisbursalConfirmation<AddComp> {
//
//    private String partnerLoanId;
//
//    @NotNull
//    private Double amount;
//
//    private String firstEMIDate;
//
//    private String loanStartDate;
//
//    private String loanProduct;
//
//    private String appFormId;
//
//    private String utr;
//
//    private String po;
//
//    private Double bpiAmount;
//
//    private Double processingFee;
//
//    private Double netDisbursalAmount;
//
//    @Size(min = 1)
//    private List<@Valid AddComp> addComps;
//
//    private String utrForeClosure;
//
//}
