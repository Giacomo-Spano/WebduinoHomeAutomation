package com.server.webduino.servlet.simulator;

import com.server.webduino.core.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
//@WebServlet(name = "SensorServlet")
public class CommandServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CommandServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String id = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();


        String json = "{\"shieldid\":0,\"enabled\":true,\"pin\":14,\"remotetemperature\":0.00,\"addr\":\"HeaterActuator-18:fe:34:d4:c6:87\",\"status\":\"idle\",\"type\":\"heater\",\"name\":\"Riscaldamento\",\"relestatus\":\"false\"}";
        out.print(json);


    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //questa servlet riceve command dalla app, dalle pagine wed e riceve status update dagli actuator diorettamente

        StringBuffer jb = new StringBuffer();
        String line = null;
        int shieldId;
        String subaddress;

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();


        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        JSONObject jsonResult = new JSONObject();
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            jsonResult.put("answer", "success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        out.print(jsonResult.toString());

    }
}
