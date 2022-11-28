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
import com.intellij.openapi.ui.Messages;
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

        return builder.toString();
    }

    private static Map<Character, Character> build() {
        Map<Character, Character> map = new HashMap<>();
        map.put('é', 'q');
        map.put('ö', 'w');
        map.put('ó', 'e');
        map.put('ê', 'r');
        map.put('å', 't');
        map.put('í', 'y');
        map.put('ã', 'u');
        map.put('ø', 'i');
        map.put('ù', 'o');
        map.put('ç', 'p');

        map.put('ô', 'a');
        map.put('û', 's');
        map.put('â', 'd');
        map.put('à', 'f');
        map.put('ï', 'g');
        map.put('ð', 'h');
        map.put('î', 'j');
        map.put('ë', 'k');
        map.put('ä', 'l');

        map.put('ÿ', 'z');
        map.put('÷', 'x');
        map.put('ñ', 'c');
        map.put('ì', 'v');
        map.put('è', 'b');
        map.put('ò', 'n');
        map.put('ü', 'm');

        Set<Character> keys = Set.copyOf(map.keySet());
        for (Character ch : keys)
            map.put((char) (ch - 32), (char) (map.get(ch) - 32));
        map.put('õ', '[');
        map.put('X', '{');
        map.put('ú', ']');
        map.put('Ú', '}');
        map.put('æ', ';');
        map.put('Æ', ':');
        map.put('ý', '\'');
        map.put('Ý', '\"');
        map.put('á', ',');
        map.put('Á', '<');
        map.put('þ', '.');
        map.put('Þ', '>');
        map.put('.', '/');
        map.put(',', '?');
        return map;
    }
}
