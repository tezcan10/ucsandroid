package com.huawei.smarthome.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huawei.smarthome.testapplication.pdp.UXACMLPDP;

import org.wso2.balana.Balana;
import org.wso2.balana.PDPConfig;

public class MainActivity extends AppCompatActivity {

    private String policy = "<Policy xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" PolicyId=\"policy2Attributes\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable\" Version=\"3.0\" >\n" +
            "<Description >Policy to be used in the case a child wants to watch a movie</Description>\n" +
            "<Target >\n" +
            "</Target>\n" +
            "<Rule Effect=\"Permit\" RuleId=\"rule-permit\" >\n" +
            "<Target >\n" +
            "<AnyOf >\n" +
            "<AllOf >\n" +
            "<Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\" >\n" +
            "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\" >watchTV</AttributeValue>\n" +
            "<AttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" MustBePresent=\"true\" >\n" +
            "</AttributeDesignator>\n" +
            "</Match>\n" +
            "</AllOf>\n" +
            "</AnyOf>\n" +
            "</Target>\n" +
            "<!-- Pre condition --><Condition>\n" +
            "<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:and\" >\n" +
            "<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\" >\n" +
            "<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\" >\n" +
            "<AttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:role\" Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" MustBePresent=\"true\" >\n" +
            "</AttributeDesignator>\n" +
            "</Apply>\n" +
            "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\" >child</AttributeValue>\n" +
            "</Apply>\n" +
            "<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:integer-greater-than\" >\n" +
            "<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:integer-one-and-only\" >\n" +
            "<AttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:3.0:environment:parentsNumber\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\" DataType=\"http://www.w3.org/2001/XMLSchema#integer\" MustBePresent=\"true\" >\n" +
            "</AttributeDesignator>\n" +
            "</Apply>\n" +
            "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#integer\" >0</AttributeValue>\n" +
            "</Apply>\n" +
            "</Apply>\n" +
            "</Condition>\n" +
            "</Rule>\n" +
            "<!-- Default rule --><Rule Effect=\"Deny\" RuleId=\"urn:oasis:names:tc:xacml:3.0:defdeny\" >\n" +
            "<Description >DefaultDeny</Description>\n" +
            "<Target >\n" +
            "</Target>\n" +
            "</Rule>\n" +
            "</Policy>\n";

    private String request = "<Request xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\"\n" +
            "\tCombinedDecision=\"false\" ReturnPolicyIdList=\"false\">\n" +
            "\n" +
            "\t<Attributes\n" +
            "\t\tCategory=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\">\n" +
            "\t\t<Attribute IncludeInResult=\"false\"\n" +
            "\t\t\tAttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\">\n" +
            "\t\t\t<AttributeValue\n" +
            "\t\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">Baby</AttributeValue>\n" +
            "\t\t</Attribute>\n" +
            "\t\t<Attribute IncludeInResult=\"false\"\n" +
            "\t\t\tAttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:role\">\n" +
            "\t\t\t<AttributeValue\n" +
            "\t\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">child</AttributeValue>\n" +
            "\t\t</Attribute>\n" +
            "\t</Attributes>\n" +
            "\n" +
            "\t<Attributes\n" +
            "\t\tCategory=\"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\">\n" +
            "\t\t<Attribute\n" +
            "\t\t\tAttributeId=\"urn:oasis:names:tc:xacml:1.0:environment:environment-id\"\n" +
            "\t\t\tIncludeInResult=\"false\">\n" +
            "\t\t\t<AttributeValue\n" +
            "\t\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">HOME</AttributeValue>\n" +
            "\t\t</Attribute>\n" +
            "\t\t<Attribute\n" +
            "\t\t\tAttributeId=\"urn:oasis:names:tc:xacml:3.0:environment:parentsNumber\"\n" +
            "\t\t\tIncludeInResult=\"false\">\n" +
            "\t\t\t<AttributeValue\n" +
            "\t\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#integer\">2</AttributeValue>\n" +
            "\t\t</Attribute>\n" +
            "\t</Attributes>\n" +
            "\n" +
            "\t<Attributes\n" +
            "\t\tCategory=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\">\n" +
            "\t\t<Attribute\n" +
            "\t\t\tAttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"\n" +
            "\t\t\tIncludeInResult=\"false\">\n" +
            "\t\t\t<AttributeValue\n" +
            "\t\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">watchTV</AttributeValue>\n" +
            "\t\t</Attribute>\n" +
            "\t</Attributes>\n" +
            "\n" +
            "\t<Attributes\n" +
            "\t\tCategory=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "\t\t<Attribute\n" +
            "\t\t\tAttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\"\n" +
            "\t\t\tIncludeInResult=\"false\">\n" +
            "\t\t\t<AttributeValue\n" +
            "\t\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">BedroomTV</AttributeValue>\n" +
            "\t\t</Attribute>\n" +
            "\t</Attributes>\n" +
            "\n" +
            "</Request>\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickPEP(View view) {
        /*Intent intent = new Intent();
        intent.setAction("com.huawei.iam.hiSmartHome.ucs.action.PEP");
        Bundle bundle = new Bundle();
        bundle.putString("policy", policy);
        bundle.putString("request", request); */
        /*EXTRA_UCS_TYPE = UCS_LOCAL;
        EXTRA_UCS_ACTION = {OPEN_APP, CLOSE_APP};
        EXTRA_UCS_APP_PACKAGE=<app_package_name>  {default value="com.huawei.iam.demoGame"};
        EXTRA_UCS_PARENT_APPROVAL_REQUIRED = {true, false,absent}*/
        /*bundle.putString("EXTRA_UCS_TYPE","UCS_LOCAL");
        bundle.putString("EXTRA_UCS_ACTION","OPEN_APP");
        bundle.putString("EXTRA_UCS_APP_PACKAGE","org.example.com");
        bundle.putString("EXTRA_UCS_PARENT_APPROVAL_REQUIRED","false,absent");
        intent.putExtras(bundle);
        sendBroadcast(intent); */
        Balana balana;
        balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();
        UXACMLPDP uxacmlpdp = new UXACMLPDP(pdpConfig);
        String response = uxacmlpdp.evaluate(request, policy);
        TextView textView = findViewById(R.id.result);
        textView.setText(response);
    }
}
