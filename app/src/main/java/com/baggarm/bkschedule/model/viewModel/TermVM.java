package com.baggarm.bkschedule.model.viewModel;

import java.util.List;

/**
 * This model use properties of SubjectViewModel to create Term
 *
 * @author DN
 * @version 2.19.1406
 * @see SubjectVM
 * @since 1.0
 */
public class TermVM {
    private String termName;
    private List<SubjectVM> termSubjects;

    public TermVM(String termName, List<SubjectVM> termSubjects) {
        this.termName = termName;
        this.termSubjects = termSubjects;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public List<SubjectVM> getTermSubjects() {
        return termSubjects;
    }

    public void setTermSubjects(List<SubjectVM> termSubjects) {
        this.termSubjects = termSubjects;
    }
}
