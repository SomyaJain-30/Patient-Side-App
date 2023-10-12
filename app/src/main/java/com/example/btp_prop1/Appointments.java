package com.example.btp_prop1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Appointments implements Parcelable {
    private String Cid, Date, day, Did, Status, Timeslot, AppointmentId, DoctorName, PatientName;
    public Appointments(){

    }

    protected Appointments(Parcel in) {
        Cid = in.readString();
        Date = in.readString();
        day = in.readString();
        Did = in.readString();
        Status = in.readString();
        Timeslot = in.readString();
        AppointmentId = in.readString();
    }

    public static final Creator<Appointments> CREATOR = new Creator<Appointments>() {
        @Override
        public Appointments createFromParcel(Parcel in) {
            return new Appointments(in);
        }

        @Override
        public Appointments[] newArray(int size) {
            return new Appointments[size];
        }
    };

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDid() {
        return Did;
    }

    public void setDid(String did) {
        Did = did;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTimeslot() {
        return Timeslot;
    }

    public void setTimeslot(String timeslot) {
        Timeslot = timeslot;
    }

    public String getAppointmentId() {
        return AppointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        AppointmentId = appointmentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Cid);
        dest.writeString(Date);
        dest.writeString(day);
        dest.writeString(Did);
        dest.writeString(Status);
        dest.writeString(Timeslot);
        dest.writeString(AppointmentId);
    }

    public String getDoctorName() {
        return DoctorName;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }
}
