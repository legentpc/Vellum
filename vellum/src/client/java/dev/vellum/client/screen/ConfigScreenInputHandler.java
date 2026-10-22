package dev.vellum.client.screen;

import dev.vellum.client.input.VellumMousePosition;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.screen.ConfigScreenState;

public final class ConfigScreenInputHandler {
    public boolean isInsideNavigation(
            VellumMousePosition mouse,
            int navigationLeft,
            int navigationTop,
            int navigationRight,
            int navigationBottom
    ) {
        return mouse.x() >= navigationLeft
                && mouse.x() < navigationRight
                && mouse.y() >= navigationTop
                && mouse.y() < navigationBottom;
    }

    public boolean isInsideContent(
            VellumMousePosition mouse,
            int contentLeft,
            int contentTop,
            int contentRight,
            int contentBottom
    ) {
        return mouse.x() >= contentLeft
                && mouse.x() < contentRight
                && mouse.y() >= contentTop
                && mouse.y() < contentBottom;
    }

    public boolean isInsideSaveButton(
            VellumMousePosition mouse,
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

        return mouse.x() >= left
                && mouse.x() < left + saveButtonWidth
                && mouse.y() >= top
                && mouse.y() < top + saveButtonHeight;
    }

    public boolean isInsideStringControl(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 210;
        int top = rowTop + 3;

        return mouse.x() >= left
                && mouse.x() < contentRight - 18
                && mouse.y() >= top
                && mouse.y() < top + optionControlHeight;
    }

    public boolean isInsideNumericMinus(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 62;
        int top = rowTop + 3;

        return mouse.x() >= left
                && mouse.x() < left + 20
                && mouse.y() >= top
                && mouse.y() < top + optionControlHeight;
    }

    public boolean isInsideNumericPlus(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 38;
        int top = rowTop + 3;

        return mouse.x() >= left
                && mouse.x() < left + 20
                && mouse.y() >= top
                && mouse.y() < top + optionControlHeight;
    }

    public int listItemIndex(
            VellumMousePosition mouse,
            int contentLeft,
            int contentRight,
            int listTop,
            int listRowHeight,
            int itemCount
    ) {
        if (mouse.x() < contentLeft + 18
                || mouse.x() >= contentRight - 150) {
            return -1;
        }

        int index =
                (int) ((mouse.y() - listTop) / listRowHeight);

        if (index < 0
                || index >= itemCount) {
            return -1;
        }

        return index;
    }

    public boolean isInsideAddButton(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop,
            int optionRowHeight,
            int optionControlHeight
    ) {
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;
        int buttonTop = rowTop + 3;

        return mouse.x() >= addLeft
                && mouse.x() < addLeft + 52
                && buttonTop <= rowTop + optionRowHeight
                && rowTop <= buttonTop + optionControlHeight;
    }

    public boolean isInsideRemoveButton(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop,
            int optionRowHeight,
            int optionControlHeight
    ) {
        int removeLeft = contentRight - 72;
        int buttonTop = rowTop + 3;

        return mouse.x() >= removeLeft
                && mouse.x() < removeLeft + 64
                && buttonTop <= rowTop + optionRowHeight
                && rowTop <= buttonTop + optionControlHeight;
    }

    public boolean isInsideMoreButton(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 84;
        int top = rowTop + 3;

        return mouse.x() >= left
                && mouse.x() < left + 52
                && mouse.y() >= top
                && mouse.y() < top + optionControlHeight;
    }

    public boolean handleNavigationClick(
            VellumMousePosition mouse,
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
                mouse.x(),
                mouse.y(),
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

            if (mouse.y() >= rowTop
                    && mouse.y() < rowBottom) {
                int categoryLeft =
                        navigationLeft
                                + categoryTextPadding
                                + category.depth()
                                * categoryIndent;

                boolean clickedIndicator =
                        category.hasChildren()
                                && mouse.x() >= categoryLeft
                                && mouse.x() < categoryLeft + 16;

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
}
