package com.example.vept.pl.L4;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlannerMainViewModel extends ViewModel {
    private Diagram dg;
    private DiagramWrapper dw;
    public PlannerMainViewModel() {
        dg = new Diagram("Hello",1f,1f);
        List<Diagram> dl = new ArrayList<>();
        dl.add(dg);
        dl.add(new Diagram("Hi",500f,500f));
        dw = new DiagramWrapper(dl);
    }

    public DiagramWrapper getDw() {
        return dw;
    }
}
