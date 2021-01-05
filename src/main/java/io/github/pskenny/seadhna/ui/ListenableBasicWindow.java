package io.github.pskenny.seadhna.ui;

import java.util.ArrayList;

import com.googlecode.lanterna.gui2.BasicWindow;

public class ListenableBasicWindow extends BasicWindow {
    ArrayList<BasicWindowListener> windowListeners = new ArrayList<>();

    public void addWindowListener(BasicWindowListener bwl) {
        windowListeners.add(bwl);
    }

    @Override
    public void close() {
        windowListeners.iterator().forEachRemaining((listener) -> {
            listener.onClosing();
        });
        super.close();
    }

    public interface BasicWindowListener {
        public void onClosing();
    }
}
