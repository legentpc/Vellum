package dev.vellum.client.screen;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.screen.ConfigScreenState;

public final class ConfigScreenInputHandler {
    public boolean isInsideNavigation(
            double mouseX,
            double mouseY,
            int navigationLeft,
            int navigationTop,
            int navigationRight,
            int navigationBottom
    ) {
        return mouseX >= navigationLeft
                && mouseX < navigationRight
                && mouseY >= navigationTop
                && mouseY < navigationBottom;
    }

    public boolean isInsideContent(
            double mouseX,
            double mouseY,
            int contentLeft,
            int contentTop,
            int contentRight,
            int contentBottom
    ) {
        return mouseX >= contentLeft
                && mouseX < contentRight
                && mouseY >= contentTop
                && mouseY < contentBottom;
    }

    public boolean isInsideSaveButton(
            double mouseX,
            double mouseY,
            int contentRight,
            int contentBottom,
            int saveButtonWidth,
            int saveButtonHeight
    ) {
        int left =
                contentRight
                        - saveButtonWidth
                        - 12;

        int top =
                contentBottom
                        - saveButtonHeight
                        - 12;

        return mouseX >= left
                && mouseX < left + saveButtonWidth
                && mouseY >= top
                && mouseY < top + saveButtonHeight;
    }

    public boolean isInsideStringControl(
            double mouseX,
            double mouseY,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 210;
        int top = rowTop + 3;

        return mouseX >= left
                && mouseX < contentRight - 18
                && mouseY >= top
                && mouseY < top + optionControlHeight;
    }

    public boolean isInsideNumericMinus(
            double mouseX,
            double mouseY,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 62;
        int top = rowTop + 3;

        return mouseX >= left
                && mouseX < left + 20
                && mouseY >= top
                && mouseY < top + optionControlHeight;
    }

    public boolean isInsideNumericPlus(
            double mouseX,
            double mouseY,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 38;
        int top = rowTop + 3;

        return mouseX >= left
                && mouseX < left + 20
                && mouseY >= top
                && mouseY < top + optionControlHeight;
    }

    public int listItemIndex(
            double mouseX,
            double mouseY,
            int contentLeft,
            int contentRight,
            int listTop,
            int listRowHeight,
            int itemCount
    ) {
        if (mouseX < contentLeft + 18
                || mouseX >= contentRight - 150) {
            return -1;
        }

        int index =
                (int) ((mouseY - listTop) / listRowHeight);

        if (index < 0
                || index >= itemCount) {
            return -1;
        }

        return index;
    }

    public boolean isInsideAddButton(
            double mouseX,
            int contentRight,
            int rowTop,
            int optionRowHeight,
            int optionControlHeight
    ) {
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;
        int buttonTop = rowTop + 3;

        return mouseX >= addLeft
                && mouseX < addLeft + 52
                && buttonTop <= rowTop + optionRowHeight
                && rowTop <= buttonTop + optionControlHeight;
    }

    public boolean isInsideRemoveButton(
            double mouseX,
            int contentRight,
            int rowTop,
            int optionRowHeight,
            int optionControlHeight
    ) {
        int removeLeft = contentRight - 72;
        int buttonTop = rowTop + 3;

        return mouseX >= removeLeft
                && mouseX < removeLeft + 64
                && buttonTop <= rowTop + optionRowHeight
                && rowTop <= buttonTop + optionControlHeight;
    }

    public boolean isInsideMoreButton(
            double mouseX,
            double mouseY,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 84;
        int top = rowTop + 3;

        return mouseX >= left
                && mouseX < left + 52
                && mouseY >= top
                && mouseY < top + optionControlHeight;
    }

    public boolean handleNavigationClick(
            double mouseX,
            double mouseY,
            VellumConfigEditor editor,
            int navigationLeft,
            int navigationTop,
            int navigationRight,
            int navigationBottom,
            int categoryRowHeight,
            int categoryTextPadding,
            int categoryIndent
    ) {
        if (!isInsideNavigation(
                mouseX,
                mouseY,
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom
        )) {
            return false;
        }

        int rowTop = navigationTop + 32;

        for (CategoryNode category
                : editor.categoryTree().visibleNodes()) {
            int rowBottom =
                    rowTop + categoryRowHeight;

            if (mouseY >= rowTop
                    && mouseY < rowBottom) {
                int categoryLeft =
                        navigationLeft
                                + categoryTextPadding
                                + category.depth()
                                * categoryIndent;

                boolean clickedIndicator =
                        category.hasChildren()
                                && mouseX >= categoryLeft
                                && mouseX < categoryLeft + 16;

                if (clickedIndicator) {
                    editor.toggle(category);
                    return true;
                }

                editor.select(category);
                return true;
            }

            rowTop += categoryRowHeight;
        }

        return false;
    }

    public boolean isInsideNavigation(
            VellumMousePosition mouse,
            int navigationLeft,
            int navigationTop,
            int navigationRight,
            int navigationBottom
    ) {
        return isInsideNavigation(
                mouse.x(),
                mouse.y(),
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom
        );
    }

    public int listItemIndex(
            VellumMousePosition mouse,
            int contentLeft,
            int contentRight,
            int listTop,
            int listRowHeight,
            int itemCount
    ) {
        return listItemIndex(
                mouse.x(),
                mouse.y(),
                contentLeft,
                contentRight,
                listTop,
                listRowHeight,
                itemCount
        );
    }
}
