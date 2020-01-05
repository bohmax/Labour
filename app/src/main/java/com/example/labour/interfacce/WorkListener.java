package com.example.labour.interfacce;

import com.example.labour.Package_item;
import java.util.List;

public interface WorkListener {

    void newWork(List<Package_item> list, int pos);

    void updateAfterStep(float coordinata);

    void workCompleted(Package_item item);
}
