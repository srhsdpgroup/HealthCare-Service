/*
 * PatientService.java
 * SRH Hochschule Heidelberg
 * All rights reserverd 2016
*/
package com.health.care.management.service;

import com.health.care.management.domain.PastAppointmentDetails;
import com.health.care.management.domain.Patient;
import com.health.care.management.dto.PatientDTO;

import java.util.List;

public interface PatientService {

    Patient findPatientByUserID(int userId);

    // add or update patient
    int savePatientInfo(PatientDTO patient);

    // to update patient
    int updatePatient(Patient patient);

    void bookAppointment();

    List<PastAppointmentDetails> fetchPastAppointmentDetials(int patientId);

}