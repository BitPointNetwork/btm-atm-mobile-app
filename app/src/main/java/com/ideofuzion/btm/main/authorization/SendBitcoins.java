package com.ideofuzion.btm.main.authorization;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ideofuzion.btm.BTMApplication;
import com.ideofuzion.btm.main.transfercomplete.TransferCompleteActivity;
import com.ideofuzion.btm.model.ServerMessage;
import com.ideofuzion.btm.network.VolleyRequestHelper;
import com.ideofuzion.btm.utils.AlertMessage;
import com.ideofuzion.btm.utils.Constants;
import com.ideofuzion.btm.utils.DialogHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ideofuzion on 6/23/2017.
 *
 * this is an activity and sending bitcoin functionality is achieved
 * in this activity
 */

public class SendBitcoins implements Response.Listener<JSONObject>, Response.ErrorListener, Constants.ResultCode {

    Context context;
    DialogHelper dialogHelper;
    View view;
    Double bitcoinsX;
    Double bitcoinY;

    /**
     * this is constructor of this class
     * it will be called each time the
     * user init this class's object
     * with these params
     *
     * @param context
     * @param view
     * @param bitcoinsX
     * @param bitcoinY
     */
    public SendBitcoins(Context context, View view, Double bitcoinsX, Double bitcoinY) {
        this.context = context;
        this.view = view;
        this.bitcoinsX = bitcoinsX;
        this.bitcoinY = bitcoinY;
    }


    /**
     * sending request send balance
     * request to server
     */
    public void sendBitcoinTransferRequestToServer() {
        try {
            dialogHelper = new DialogHelper(context);
            dialogHelper.showProgressDialog();

            String url = Constants.BASE_SERVER_URL + Constants.SEND_BALANCE;

            Map<String, String> map = new HashMap<>();
            map.put("customerAddress", BTMApplication.getInstance().getQrModel().getBitcoin());
            map.put("amount",bitcoinsX+"");
            map.put("merchantProfit",bitcoinY+"");

            map.put("merchantUserName", BTMApplication.getInstance().getBTMUserObj().getUserName());
            map.put("merchantPassword", BTMApplication.getInstance().getBTMUserObj().getUserPassword());
            //map.put("merchantPassword", SessionManager.getInstance(this).getPass());

            VolleyRequestHelper.sendPostRequestWithParam(url, map, this);

        } catch (Exception e) {
        }
    }

    /**
     this function will be called when the server throws an
     * error when failed to connect to server
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        if (dialogHelper != null) {
            dialogHelper.hideProgressDialog();
        }
        AlertMessage.showError(view, Constants.ERROR_CHECK_INTERNET);

    }

    /**
     * this function will be called each time when server
     * successfully executes over request
     *
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {
        if (dialogHelper != null) {
            dialogHelper.hideProgressDialog();
        }
        if (response != null) {
            ServerMessage serverMessageResponse = new ServerMessage();
            try {
                serverMessageResponse.setData(response.getString("data"));
                serverMessageResponse.setCode(response.getInt("code"));
                serverMessageResponse.setMessage(response.getString("message"));
                if (serverMessageResponse.getCode() == CODE_SUCCESS) {
                    context.startActivity(new Intent(context, TransferCompleteActivity.class)
                    .putExtra("transactionId",serverMessageResponse.getData().replace("{","").replace("}","").replaceAll(" ","")));

                    //redirectUserAfterSuccessSignIn(serverMessageResponse.getData(), null);
                } else {
                    AlertMessage.showError(view, serverMessageResponse.getMessage());
                }//end oe else
            } catch (Exception e) {
                e.printStackTrace();
            }

        }//end of if for response not null
    }
}
