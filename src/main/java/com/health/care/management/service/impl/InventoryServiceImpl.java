package com.health.care.management.service.impl;

import com.health.care.management.dao.DiagnosisDAO;
import com.health.care.management.dao.InventoryDAO;
import com.health.care.management.dao.ReportingDao;
import com.health.care.management.dao.impl.DiagnosisDAOImpl;
import com.health.care.management.dao.impl.InventoryDAOImpl;
import com.health.care.management.dao.impl.ReportingDaoImpl;
import com.health.care.management.domain.Bill;
import com.health.care.management.domain.Inventory;
import com.health.care.management.service.InventoryService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InventoryServiceImpl implements InventoryService {

    private InventoryDAO inventoryDao;
    private DiagnosisDAO diagnosisDao;
    private ReportingDao reportingDao;

    public InventoryServiceImpl() {
        this.inventoryDao = new InventoryDAOImpl();
        this.diagnosisDao = new DiagnosisDAOImpl();
        this.reportingDao = new ReportingDaoImpl();
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryDao.fetchAllInventory();
    }

    @Override
    public int updateInventory(Inventory inventory) {
        return inventoryDao.updatedInventory(inventory);
    }

    @Override
    public int saveInventory(Inventory inventory) {
        return inventoryDao.addInventory(inventory);
    }

    @Override
    public List<Bill> fetchAllDiagnoisedDetail(String status) {
        List<Map<String, Object>> returnedValue = diagnosisDao.fetchTreatmentDetailsByStatus(status);
        List<Bill> listOfDiagnosiedDetails = new ArrayList<>();

        returnedValue.forEach(a -> {
            Bill bill = new Bill();
            bill.setPrescription(a.get("prescription").toString());
            bill.setDoctorName("Dr. " + a.get("d_first_name").toString() + " " + a.get("d_last_name").toString());
            String salutation = "Miss. ";
            if (Boolean.valueOf(a.get("sex").toString())) {
                salutation = "Mr. ";
            }
            bill.setPatientId(Integer.valueOf(a.get("patient_id").toString()));
            bill.setPatientName(salutation + a.get("p_first_name").toString() + " " + a.get("p_last_name").toString());
            // null check only for admite date. coz if admit date is present doctor will have provided the discharge date.
            if (null != a.get("admit_date")) {
                bill.setAdmitDate(new Date(a.get("admit_date").toString()));
                bill.setDischargeDate(new Date(a.get("discharge_date").toString()));
            }
            bill.setDoctorSpecialization(a.get("specialization").toString());
            bill.setDiagnosisId(Integer.valueOf(a.get("diagnosis_id").toString()));
            listOfDiagnosiedDetails.add(bill);
        });
        return listOfDiagnosiedDetails;
    }

    @Override
    public int saveBill(Bill bill) {
        // calculate number of days admitted
        // create bill ammount save bill datble

        double doctorConsultingFees = 400;
        double bedCharge = 1500;
        int numberOfDaysAdmitted = 0;
        if (bill.getAdmitDate() != null) {
            long diff = Math.abs(bill.getAdmitDate().getTime() - bill.getDischargeDate().getTime());
            numberOfDaysAdmitted = Math.toIntExact(diff / (24 * 60 * 60 * 1000));
        }
        double billAmount = doctorConsultingFees + (bedCharge * numberOfDaysAdmitted);
        bill.setBillAmount(billAmount);
        bill.setStatus("billed");
        inventoryDao.saveBill(bill);

        // Query will be made based on status to fetch the diagnosied items when it is showing treated. if billed it ll not be quried

        return diagnosisDao.updateTheDiagnosisStatus("billed", bill.getDiagnosisId());

    }

    @Override
    public String generateReport(int selectedReportvalue) {
        String locationOfPdf = null;
        switch (selectedReportvalue) {

        // "Hi Welcome to report generating srevice"
        // + "\nSelect '1' to generate report for all available doctors"
        // + "\nSelect '2'for report of all patient"
        // + "\nSelect '3'for report of all inventories"
        // + "\nSelect '4' for report of particular patient"
        case 1: {
            locationOfPdf = reportingDao.generateListOfDoctorReport();
            break;
        }
        case 2: {
            locationOfPdf = reportingDao.generateDetailsOfAllPatient();
            break;
        }
        case 3: {
            locationOfPdf = reportingDao.generateInventoryReport();
            break;

        }
        default: {
            locationOfPdf = reportingDao.generateReportOfSinglePatient(selectedReportvalue);
        }
        }

        return locationOfPdf;
    }

}
