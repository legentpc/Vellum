package dev.vellum.client.focus;

public final class VellumFocusManager {
    private int focusedIndex;
    private int optionCount;

    public void setOptionCount(int optionCount) {
        this.optionCount = Math.max(
                0,
                optionCount
        );

        if (focusedIndex >= this.optionCount) {
            focusedIndex =
                    Math.max(
                            0,
                            this.optionCount - 1
                    );
        }
    }

    public int focusedIndex() {
        return focusedIndex;
    }

    public void focusFirst() {
        if (optionCount > 0) {
            focusedIndex = 0;
        }
    }

    public void focusNext() {
        if (optionCount == 0) {
            return;
        }

        focusedIndex =
                Math.min(
                        focusedIndex + 1,
                        optionCount - 1
                );
    }

    public void focusPrevious() {
        if (optionCount == 0) {
            return;
        }

        focusedIndex =
                Math.max(
                        focusedIndex - 1,
                        0
                );
    }

    public void clearFocus() {
        focusedIndex = -1;
    }

    public boolean hasFocus() {
        return focusedIndex >= 0
                && focusedIndex < optionCount;
    }

    public void focusIndex(int index) {
        if (index >= 0
                && index < optionCount) {
            focusedIndex = index;
        }
    }
}
