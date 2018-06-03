package com.readysteadythink.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.ListIterator;

public class UIElementManager {
    private ArrayList<IUIElement> elements = new ArrayList<IUIElement>();

    public void update(float dt) {
        ListIterator<IUIElement> itr = elements.listIterator();
        while (itr.hasNext()) {
            itr.next().update(dt);
        }
    }

    public void draw(SpriteBatch batch, Viewport viewport) {
        ListIterator<IUIElement> itr = elements.listIterator();
        while (itr.hasNext()) {
            itr.next().draw(batch, viewport);
        }
    }

    public void add(IUIElement element) {
        elements.add(element);
    }

    public String firstHit(float x, float y) {
        ListIterator<IUIElement> itr = elements.listIterator();
        while (itr.hasNext()) {
            IUIElement element = itr.next();
            if (element.isIn(x, y)) {
                return element.getName();
            }
        }
        return null;
    }

    public IUIElement get(String name) {
        ListIterator<IUIElement> itr = elements.listIterator();
        while (itr.hasNext()) {
            IUIElement element = itr.next();
            if (element.getName() == name) {
                return element;
            }
        }
        return null;
    }

    public boolean isHit(String name, float x, float y) {
        ListIterator<IUIElement> itr = elements.listIterator();
        while (itr.hasNext()) {
            IUIElement element = itr.next();
            if (element.getName() == name && element.isIn(x, y)) {
                return true;
            }
        }
        return false;
    }
}
