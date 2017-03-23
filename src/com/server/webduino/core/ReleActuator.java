package com.server.webduino.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

public class ReleActuator extends Actuator /*implements TemperatureSensor.TemperatureSensorListener*/ {
    static final String STATUS_ON = "on";
    static final String STATUS_OFF = "off";

    private static final Logger LOGGER = Logger.getLogger(ReleActuator.class.getName());

    protected boolean on;

    public ReleActuator() {
        super();
        type = "releactuator";
        statusUpdatePath = "/relestatus";
    }

    @Override
    public void addListener(ActuatorListener toAdd) {
        listeners.add((HeaterActuatorListener) toAdd);
    }

    public boolean getReleStatus() {
        return on;
    }

    protected void setReleStatus(boolean on) {

        boolean oldReleStatus = this.on;
        this.on = on;

        /*if (on != oldReleStatus) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners)
                hl.changeReleStatus(on, oldReleStatus);
        }*/
    }

    @Override
    public void writeDataLog(String event) {
        HeaterDataLog dl = new HeaterDataLog();
        dl.writelog(event, this);
    }

    public Boolean sendCommand(String command, boolean status) {

        ReleActuatorCommand cmd = new ReleActuatorCommand();
        cmd.command = command;
        cmd.status = on;
        return sendCommand(cmd);
    }

    @Override
    public ActuatorCommand getCommandFromJson(JSONObject json) {
        HeaterActuatorCommand command = new HeaterActuatorCommand();
        if (command.fromJson(json))
            return command;
        else
            return null;
    }

    @Override
    public Boolean sendCommand(ActuatorCommand cmd) {
        // sendcommand è usata anche da actuatorservlet per mandare i command dalle app
        HeaterActuatorCommand heaterActuatorCommand = (HeaterActuatorCommand) cmd;

        LOGGER.info("sendCommand " +
                "\ncommand = " + heaterActuatorCommand.command +
                "\nduration = " + heaterActuatorCommand.duration +
                "\ntargetTemperature = " + heaterActuatorCommand.targetTemperature +
                "\nremoteSensor = " + heaterActuatorCommand.remoteSensor +
                "\nactiveProgramID = " + heaterActuatorCommand.activeProgramID +
                "\nactiveTimeRangeID = " + heaterActuatorCommand.activeTimeRangeID +
                "\nactiveSensorID = " + heaterActuatorCommand.activeSensorID +
                "\nactiveSensorTemperature = " + heaterActuatorCommand.activeSensorTemperature);

        if (isUpdated()) {// controlla se l'actuator è in stato updated (cioè non è offline)

            // se non sono cambiati i parametri del programma già attivo non inviare
            if (noProgramDataChanges(heaterActuatorCommand) &&
                    (heaterActuatorCommand.command == HeaterActuatorCommand.Command_Program_ReleOn ||
                            heaterActuatorCommand.command == HeaterActuatorCommand.Command_Program_ReleOff)) {
                LOGGER.info("skip send command " + heaterActuatorCommand.command + "no program changes");
                //writeDataLog("skip send command " + heaterActuatorCommand.command + "no changes" );
                return false;
            }
        }
        setActiveSensorID(heaterActuatorCommand.activeSensorID);
        writeDataLog("Sending command" + heaterActuatorCommand.command);

        String postParam = "";
        String path = "";
        String strEvent = "event";

        LOGGER.info("sendCommand command=" + heaterActuatorCommand.command + ",duration=" + heaterActuatorCommand.duration + ",targetTemperature=" + heaterActuatorCommand.targetTemperature + ",remoteSensor=" + heaterActuatorCommand.remoteSensor +
                ",activeProgramID=" + heaterActuatorCommand.activeProgramID + ",activeTimeRangeID=" + heaterActuatorCommand.activeTimeRangeID + ",activeSensorID=" + heaterActuatorCommand.activeSensorID + "activeSensorTemperature=" + heaterActuatorCommand.activeSensorTemperature);

        return heaterActuatorCommand.send(this);

/*
        if (heaterActuatorCommand.command.equals(HeaterActuatorCommand.Command_Program_ReleOn)) {
            strEvent = "Command_Program_ReleOn";
            path = "/rele";
            postParam = "status=" + HeaterActuatorCommand.Command_Program_ReleOn;
            postParam += "&duration=" + heaterActuatorCommand.duration;
            postParam += "&target=" + heaterActuatorCommand.targetTemperature;
            if (!heaterActuatorCommand.remoteSensor) {
                postParam += "&localsensor=1";
            } else {
                postParam += "&localsensor=0";
            }
            postParam += "&sensor=" + heaterActuatorCommand.activeSensorID;
            postParam += "&program=" + heaterActuatorCommand.activeProgramID;
            postParam += "&timerange=" + heaterActuatorCommand.activeTimeRangeID;
            postParam += "&temperature=" + heaterActuatorCommand.activeSensorTemperature;
            postParam += "&json=1";

        } else if (heaterActuatorCommand.command.equals(HeaterActuatorCommand.Command_Program_ReleOff)) {
            strEvent = "Command_Program_ReleOff";
            path = "/rele";
            postParam = "status=" + HeaterActuatorCommand.Command_Program_ReleOff;
            postParam += "&duration=" + heaterActuatorCommand.duration;
            postParam += "&target=" + heaterActuatorCommand.targetTemperature;
            if (!heaterActuatorCommand.remoteSensor) {
                postParam += "&localsensor=1";
            } else {
                postParam += "&localsensor=0";
            }
            postParam += "&sensor=" + heaterActuatorCommand.activeSensorID;
            postParam += "&program=" + heaterActuatorCommand.activeProgramID;
            postParam += "&timerange=" + heaterActuatorCommand.activeTimeRangeID;
            postParam += "&temperature=" + heaterActuatorCommand.activeSensorTemperature;
            postParam += "&json=1";

        } else if (heaterActuatorCommand.command.equals(HeaterActuatorCommand.Command_Manual_Auto)) {
            strEvent = "Command_Manual_Auto";
            path = "/rele";
            postParam = "status=" + HeaterActuatorCommand.Command_Manual_Auto;
            postParam += "&duration=" + heaterActuatorCommand.duration;
            if (!heaterActuatorCommand.remoteSensor) {
                postParam += "&localsensor=1";
            } else {
                postParam += "&localsensor=0";
            }
            postParam += "&sensor=" + heaterActuatorCommand.activeSensorID;
            postParam += "&manual=1";
            postParam += "&temperature=" + heaterActuatorCommand.activeSensorTemperature;
            postParam += "&json=1";

        } else if (heaterActuatorCommand.command.equals(HeaterActuatorCommand.Command_Manual_Off)) {
            strEvent = "Command_Manual_Off";
            path = "/rele";
            postParam = "status=" + HeaterActuatorCommand.Command_Manual_Off;
            postParam += "&duration=" + heaterActuatorCommand.duration;
            postParam += "&manual=2";
            postParam += "&json=1";

        } else if (heaterActuatorCommand.command.equals(HeaterActuatorCommand.Command_Manual_End)) {
            strEvent = "Command_Manual_End";
            path = "/rele";
            postParam = "status=" + HeaterActuatorCommand.Command_Manual_End;
            postParam += "&manual=3";
            postParam += "&temperature=" + heaterActuatorCommand.activeSensorTemperature;
            postParam += "&json=1";

        } else if (heaterActuatorCommand.command.equals(HeaterActuatorCommand.Command_Send_Temperature)) {
            strEvent = "Command_Send_Temperature";
            path = "/temp";
            postParam = "temperature=" + heaterActuatorCommand.activeSensorTemperature;
            if (localSensor == true)
                postParam += "&localsensor=0";
            else
                postParam += "&localsensor=1";
            postParam += "&sensor=" + heaterActuatorCommand.activeSensorID;
        }

        boolean res = postCommand(postParam, path);

        if (res) {
            //writeDataLog(strEvent + " sent");
            LOGGER.info("Command=" + heaterActuatorCommand.command + " sent");
        } else {
            //writeDataLog(strEvent + " FAILED");
            LOGGER.info("Command=" + heaterActuatorCommand.command + " failed");
        }
        return res;
        */
    }

    public boolean noProgramDataChanges(HeaterActuatorCommand heaterActuatorCommand) {
        return heaterActuatorCommand.duration * 60 == duration &&
                heaterActuatorCommand.targetTemperature == targetTemperature &&
                heaterActuatorCommand.remoteSensor == !isLocalSensor() &&
                heaterActuatorCommand.activeProgramID == activeProgramID &&
                heaterActuatorCommand.activeTimeRangeID == activeTimeRangeID &&
                heaterActuatorCommand.activeSensorID == activeSensorID &&
                (heaterActuatorCommand.remoteSensor && heaterActuatorCommand.activeSensorTemperature == remoteTemperature);
    }

    @Override
    public void updateFromJson(Date date, JSONObject json) {

        boolean oldReleStatus = this.releStatus;
        int oldProgramId = activeProgramID;
        int oldTimerangeId = activeTimeRangeID;
        int oldsensorId = activeSensorID;
        double oldTargetId = targetTemperature;
        String oldStatus = getStatus();
        double oldRemoteTemperature = -1;

        lastUpdate = date;
        online = true;
        try {
            LOGGER.info("received jsonResultSring=" + json.toString());

            /*if (json.has("temperature"))
                setTemperature(json.getDouble("temperature"));
            if (json.has("avtemperature"))
                setAvTemperature(json.getDouble("avtemperature"));*/
            if (json.has("remotetemperature")) {
                oldRemoteTemperature = getRemoteTemperature();
                setRemoteTemperature(json.getDouble("remotetemperature"));
            }
            if (json.has("relestatus"))
                setReleStatus(json.getBoolean("relestatus"));
            if (json.has("status"))
                setStatus(json.getString("status"));
            if (json.has("name"))
                setName(json.getString("name"));
            if (json.has("sensorid"))
                setId(json.getInt("sensorid"));
            if (json.has("duration"))
                setDuration(duration = json.getInt("duration"));
            if (json.has("remaining"))
                setRemaining(remaining = json.getInt("remaining"));
            if (json.has("localsensor"))
                setLocalSensor(json.getBoolean("localsensor"));
            if (json.has("target"))
                setTargetTemperature(targetTemperature = json.getDouble("target"));
            if (json.has("program"))
                setActiveProgramID(activeProgramID = json.getInt("program"));
            if (json.has("timerange"))
                setActiveTimeRangeID(activeTimeRangeID = json.getInt("timerange"));
        } catch (JSONException e) {
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
            writeDataLog("error");
        }

        if (releStatus != oldReleStatus) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners) {
                ((HeaterActuatorListener) hl).changeReleStatus(releStatus, oldReleStatus);
            }
        }
        if (activeProgramID != oldProgramId || activeTimeRangeID != oldTimerangeId) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners)
                ((HeaterActuatorListener) hl).changeProgram(this, activeProgramID, oldProgramId, activeTimeRangeID, oldTimerangeId);
        }
        if (!getStatus().equals(oldStatus)) {
            // Notify everybody that may be interested.
            for (ActuatorListener hl : listeners) {
                // notifica Programs che è cambiato lo stato ed invia una notifica alle app
                ((HeaterActuatorListener) hl).changeStatus(getStatus(), oldStatus);
            }
        }

        writeDataLog("update");
        LOGGER.info("updateFromJson HeaterActuator old=" + oldRemoteTemperature + "new " + getRemoteTemperature());

    }

    @Override
    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("temperature", temperature);
            json.put("avtemperature", avTemperature);
            json.put("remotetemperature", remoteTemperature);
            json.put("name", name);
            json.put("status", getStatus());
            json.put("duration", duration);
            json.put("remaining", getRemaining());
            json.put("relestatus", getReleStatus());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (lastUpdate != null)
                json.put("lastupdate", df.format(lastUpdate));
            json.put("localsensor", localSensor);

            json.put("target", targetTemperature);


            json.put("program", activeProgramID);
            Program program = Core.getProgramFromId(activeProgramID);
            if (program != null) {
                json.put("programname", program.name);
                json.put("timerange", activeTimeRangeID);

                TimeRange timeRange = program.getTimeRangeFromId(activeTimeRangeID);
                if (timeRange != null)
                    json.put("timerangename", timeRange.name);
            }

            json.put("sensorID", activeSensorID);
            SensorBase sensor = Core.getSensorFromId(activeSensorID);
            if (sensor != null)
                json.put("sensorIDname", sensor.name);

            Date currentDate = Core.getDate();

            Locale.setDefault(Locale.ITALIAN);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy");
            String strDate = sdf.format(currentDate);
            json.put("fulldate", strDate);

            sdf = new SimpleDateFormat("dd-MM-yyyy");
            strDate = sdf.format(currentDate);
            json.put("date", strDate);

            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
            strDate = sdf.format(currentDate);
            json.put("UTCdate", strDate);

            sdf = new SimpleDateFormat("hh:mm:ss");
            String strTime = sdf.format(currentDate);
            json.put("time", strTime);

            json.put("shieldid", shieldid);
            json.put("online", online);

            json.put("type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    interface HeaterActuatorListener extends ActuatorListener {
        void changeStatus(String newStatus, String oldStatus);

        void changeReleStatus(boolean newReleStatus, boolean oldReleStatus);

        void changeProgram(ReleActuator heater, int newProgram, int oldProgram, int newTimerange, int oldTimerange);


    }

}
