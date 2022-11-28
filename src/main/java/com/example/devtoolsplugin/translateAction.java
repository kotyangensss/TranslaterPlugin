package com.example.devtoolsplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class translateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        assert editor != null;
        Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        if (start == end) {
            primaryCaret.setSelection(primaryCaret.getVisualLineStart(), end);
            WriteCommandAction.runWriteCommandAction(project, () ->
                    document.replaceString(primaryCaret.getVisualLineStart(), end, convert(Objects.requireNonNull(editor.getSelectionModel().getSelectedText())))
            );
        } else {
            WriteCommandAction.runWriteCommandAction(project, () ->
                    document.replaceString(start, end, convert(Objects.requireNonNull(editor.getSelectionModel().getSelectedText())))
            );
        }


        // De-select the text range that was just replaced
        primaryCaret.removeSelection();
    }

    private static String convert(String message) {
        Map<Character, Character> translator = build();
        String[] words = message.split(" ");

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (!word.matches(".*\\p{InCyrillic}.*")) {
                builder.append(word);
                builder.append(' ');
                continue;
            }
            for (int i = 0; i < word.length(); i++) {
                if (translator.containsKey(word.charAt(i)))
                    builder.append(translator.get(word.charAt(i)));
                else
                    builder.append(word.charAt(i));

            }
            if (!word.equals(words[words.length - 1]))
                builder.append(' ');
        }

        builder.append(" ".repeat(Math.max(0, message.length() - builder.length())));
        return builder.toString();
    }

    private static Map<Character, Character> build() {
        Map<Character, Character> map = new HashMap<>();
        map.put('й', 'q');
        map.put('ц', 'w');
        map.put('у', 'e');
        map.put('к', 'r');
        map.put('е', 't');
        map.put('н', 'y');
        map.put('г', 'u');
        map.put('ш', 'i');
        map.put('щ', 'o');
        map.put('з', 'p');

        map.put('ф', 'a');
        map.put('ы', 's');
        map.put('в', 'd');
        map.put('а', 'f');
        map.put('п', 'g');
        map.put('р', 'h');
        map.put('о', 'j');
        map.put('л', 'k');
        map.put('д', 'l');

        map.put('я', 'z');
        map.put('ч', 'x');
        map.put('с', 'c');
        map.put('м', 'v');
        map.put('и', 'b');
        map.put('т', 'n');
        map.put('ь', 'm');

        Set<Character> keys = Set.copyOf(map.keySet());
        for (Character ch : keys)
            map.put((char) (ch - 32), (char) (map.get(ch) - 32));
        map.put('х', '[');
        map.put('X', '{');
        map.put('ъ', ']');
        map.put('Ъ', '}');
        map.put('ж', ';');
        map.put('Ж', ':');
        map.put('э', '\'');
        map.put('Э', '\"');
        map.put('ё', '\\');
        map.put('Ё', '|');
        map.put('б', ',');
        map.put('Б', '<');
        map.put('ю', '.');
        map.put('Ю', '>');
        map.put(']', '`');
        map.put('[', '~');
        return map;
    }
}
