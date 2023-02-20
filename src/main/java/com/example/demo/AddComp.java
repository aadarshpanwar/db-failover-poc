//package com.example.demo;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Pattern;
//
//@Getter
//@Setter
//@EqualsAndHashCode
//@ToString
//public class AddComp {
//    @NotBlank(message = "compName cannot be blank")
//    @Pattern(regexp = "insurance|facilitationFees|foreclosure|cpp|fhr", message = "comp name can be insurance or facilitationFees or foreclosure or cpp or fhr")
//    private String compName;
//    @NotNull(message = "compAmount cannot be null")
//    private Double compAmount;
//}
