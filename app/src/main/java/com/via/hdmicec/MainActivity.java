package com.via.hdmicec;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity{
    ViaCecDevice viacecdevice;
    long nativePtr;
    private TextView tv;
    private Button btGetVersion;
    private Button btGetVendorId;
    private Button btGetPhysicalAddr;
    private Button btHDMI1on; //btSendMessageTest
    private Button btHDMI1off;
    private Button btHDMI2on;
    private Button btHDMI2off;
    private Button btIsConnected;
    private Button sw1tosrc1;
    private Button sw1tosrc2;
    private Button sw2tosrc1;
    private Button sw2tosrc2;
    private ToggleButton hdmi1btCecOnOff; //btCecOnOff
    private ToggleButton hdmi2btCecOnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btGetVersion = (Button)findViewById(R.id.button);
        btGetVersion.setOnClickListener(btGetVersionOnClick);

        btGetVendorId = (Button)findViewById(R.id.button2);
        btGetVendorId.setOnClickListener(btGetVendorIdOnClick);

        btGetPhysicalAddr = (Button)findViewById(R.id.button3);
        btGetPhysicalAddr.setOnClickListener(btGetPhysicalAddrOnClick);

        btIsConnected = (Button)findViewById(R.id.button5);
        btIsConnected.setOnClickListener(btIsConnectedOnClick);

        hdmi1btCecOnOff = (ToggleButton)findViewById(R.id.toggleButton);
        hdmi1btCecOnOff.setOnClickListener(btCecOnOffOnClick1);
        hdmi2btCecOnOff = (ToggleButton)findViewById(R.id.toggleButton2);
        hdmi2btCecOnOff.setOnClickListener(btCecOnOffOnClick2);

        sw1tosrc1 = (Button)findViewById(R.id.button6);
        sw1tosrc1.setOnClickListener(btIsSW1toSRC1);
        sw1tosrc2 = (Button)findViewById(R.id.button11);
        sw1tosrc2.setOnClickListener(btIsSW1toSRC2);

        btHDMI1on = (Button)findViewById(R.id.button4);
        btHDMI1on.setOnClickListener(btHDMI1onOnClick);
        btHDMI1off = (Button)findViewById(R.id.button7);
        btHDMI1off.setOnClickListener(btHDMI1offOnClick);

        sw2tosrc1 = (Button)findViewById(R.id.button10);
        sw2tosrc1.setOnClickListener(btIsSW2toSRC1);
        sw2tosrc2 = (Button)findViewById(R.id.button12);
        sw2tosrc2.setOnClickListener(btIsSW2toSRC2);

        btHDMI2on = (Button)findViewById(R.id.button8);
        btHDMI2on.setOnClickListener(btHDMI2onOnClick);
        btHDMI2off = (Button)findViewById(R.id.button9);
        btHDMI2off.setOnClickListener(btHDMI2offOnClick);


        tv = (TextView)findViewById(R.id.textView);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        viacecdevice = new ViaCecDevice();

        nativePtr = viacecdevice.InitCec(viacecdevice, new TypeCecCallback() {

            @Override
            public void onMessageCallback(final int srcAddress, final int dstAddress, final byte[] body, final int port) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String command = String.format("[R] From %x to %x:", srcAddress, dstAddress);
                        for (int i = 0; i < body.length; i++) {
                            command = command.concat(String.format(" %02x", body[i]));
                        }

                        UpdateTextView(command + "\n");
                    }
                });
            }

            @Override
            public void onHotplugCallback(final int port, final boolean connected) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String logMessage = String.format("Hotplug event:[port:%d, connected:%b]", port, connected);
                        UpdateTextView(logMessage);
                    }
                });
            }
        }, "com/via/hdmicec/HdmiPortInfo");

        //hdmi1
        viacecdevice.ClearLogicalAddress(nativePtr, 0);
        viacecdevice.AddLogicalAddress(nativePtr, 4, 0);
        //hdmi2
        viacecdevice.ClearLogicalAddress(nativePtr, 1);
        viacecdevice.AddLogicalAddress(nativePtr, 8, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //hdmi1
        viacecdevice.ClearLogicalAddress(nativePtr, 0);
        viacecdevice.SetOption(nativePtr, OptionFlag.HDMI_OPTION_ENABLE_CEC.ordinal(), 0);
        //hdmi2
        viacecdevice.ClearLogicalAddress(nativePtr, 1);
        viacecdevice.SetOption(nativePtr, OptionFlag.HDMI_OPTION_ENABLE_CEC.ordinal(), 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Button.OnClickListener btGetVersionOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            String text = String.format("CEC version is %x\n", viacecdevice.GetVersion(nativePtr));
            UpdateTextView(text);
        }
    };
    private Button.OnClickListener btGetVendorIdOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            String text = String.format("CEC vendor id is %x\n", viacecdevice.GetVendorId(nativePtr));
            UpdateTextView(text);
        }
    };
    private Button.OnClickListener btGetPhysicalAddrOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            String text = String.format("HDMI1 CEC physical address is %x\n", viacecdevice.GetPhysicalAddress(nativePtr, 0));
            UpdateTextView(text);
            String text2 = String.format("HDMI2 CEC physical address is %x\n", viacecdevice.GetPhysicalAddress(nativePtr, 1));
            UpdateTextView(text2);
        }
    };
    private ToggleButton.OnClickListener btCecOnOffOnClick1 = new ToggleButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(hdmi1btCecOnOff.isChecked()) {    //On
                viacecdevice.SetOption(nativePtr, OptionFlag.HDMI_OPTION_ENABLE_CEC.ordinal(), 1);
                UpdateTextView("Enable HDMI1 CEC function\n");
            }
            else {                          //Off
                viacecdevice.SetOption(nativePtr, OptionFlag.HDMI_OPTION_ENABLE_CEC.ordinal(), 0);
                UpdateTextView("Disable HDMI1 CEC function\n");
            }
        }
    };
    private ToggleButton.OnClickListener btCecOnOffOnClick2 = new ToggleButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(hdmi2btCecOnOff.isChecked()) {    //On
                viacecdevice.SetOption(nativePtr, OptionFlag.HDMI_OPTION_ENABLE_CEC.ordinal(), 3);
                UpdateTextView("Enable HDMI2 CEC function\n");
            }
            else {                          //Off
                viacecdevice.SetOption(nativePtr, OptionFlag.HDMI_OPTION_ENABLE_CEC.ordinal(), 2);
                UpdateTextView("Disable HDMI2 CEC function\n");
            }
        }
    };

    private Button.OnClickListener btIsSW1toSRC1 = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {-126,0x10,0x00};
            viacecdevice.SendCecCommand(nativePtr, 4, 15, message, 0);
            String text = String.format("Send cec command 4 -> 15 0x82 0x10 0x00\n");
            UpdateTextView(text);

        }
    };

    private Button.OnClickListener btIsSW1toSRC2 = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {-126,0x20,0x00};
            viacecdevice.SendCecCommand(nativePtr, 4, 15, message, 0);
            String text = String.format("Send cec command 4 -> 15 0x82 0x20 0x00\n");
            UpdateTextView(text);

        }
    };

    private Button.OnClickListener btHDMI1onOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {0x04};
            viacecdevice.SendCecCommand(nativePtr, 4, 0, message, 0);
            String text = String.format("Send cec command 4->0 0x04\n");
            UpdateTextView(text);

        }
    };

    private Button.OnClickListener btHDMI1offOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {0x36};
            viacecdevice.SendCecCommand(nativePtr, 4, 0, message, 0);
            String text = String.format("Send cec command 4->0 0x36\n");
            UpdateTextView(text);
        }
    };

    private Button.OnClickListener btIsSW2toSRC1 = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {-126,0x10,0x00};
            viacecdevice.SendCecCommand(nativePtr, 8, 15, message, 1);
            String text = String.format("Send cec command 8->15 0x82 0x10 0x00 \n");
            UpdateTextView(text);

        }
    };

    private Button.OnClickListener btIsSW2toSRC2 = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {-126,0x20,0x00};
            viacecdevice.SendCecCommand(nativePtr, 8, 15, message, 1);
            String text = String.format("Send cec command 8->15 0x82 0x20 0x00 \n");
            UpdateTextView(text);

        }
    };

    private Button.OnClickListener btHDMI2onOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {


            byte[] message = {0x04};
            viacecdevice.SendCecCommand(nativePtr, 8, 0, message, 1);
            String text = String.format("Send cec command 8->0 0x04 \n");
            UpdateTextView(text);

        }
    };

    private Button.OnClickListener btHDMI2offOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            byte[] message = {0x36};
            viacecdevice.SendCecCommand(nativePtr, 8, 0, message, 1);
            String text = String.format("Send cec command 8->0 0x36\n");
            UpdateTextView(text);
        }
    };

    private Button.OnClickListener btIsConnectedOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            String text,text2;
            boolean ret = viacecdevice.IsConnected(nativePtr, 0);
            if(ret) {
                text = String.format("HDMI1 is connected\n");
            }
            else{
                text = String.format("HDMI1 is not connected\n");
            }
            UpdateTextView(text);
            boolean ret2 = viacecdevice.IsConnected(nativePtr, 1);
            if(ret2) {
                text2 = String.format("HDMI2 is connected\n");
            }
            else{
                text2 = String.format("HDMI2 is not connected\n");
            }
            UpdateTextView(text2);


        }
    };
    private Button.OnClickListener btGetPortInfoOnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            HdmiPortInfo[] portInfo = viacecdevice.GetPortInfos(nativePtr);
        }
    };

    private void UpdateTextView(String context) {
        tv.setText(context + tv.getText());
    }
}
