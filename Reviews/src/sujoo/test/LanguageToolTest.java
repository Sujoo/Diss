package sujoo.test;

import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class LanguageToolTest {

    public static void main(String[] args) {
        try {
            JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
            langTool.activateDefaultPatternRules();
            //String text = "Today was the best day ever, I mean the best day ever! I will tell you why! It started this morning.\nThis morning I heard a load rumbling noise near my drive way. I slowley opened my door and walked down the hallway. Once I got to the door I peeked out side to see who was there. I could not belive it my dad! He was wereing a tux and was carrying a small black box it has silver linening. The black part looked like shining velvet.\nI opened the door and ran to my dad. He asked, \"were is your mom.\"  I told him that she was watering the plants behind the house. At that he walked around the house. Then Bandit came trotting twerds me he licked my face non-stop. I quickly ran around the corner to see what was happening. I saw my dad on his knees and said the mgic word, \"Will you marry me.\" Mom screemed and agreed. Now we're a big happy family.";
            String text = "There was a old house on main street. Then one day a driver crashed into the house. The car was takein to the scrap yard. The peren was dead.\nThey say the peron haults the old house and the old car does to. Every night in a mouth they go around the town and kill people. A pumer named Luigl was takeing a walk with his brother Mario when the ghot man came and took mario away. He was taken to the haunted house. Luigl went to the house and found a special vacuum and went into the house and he suct up all of the ghosts. When he suct up the ghost car and man. He found the key and found marioo and made the house much better.";
            int offset = 0;
            List<RuleMatch> matches = langTool.check(text);
            for (RuleMatch match : matches) {
                System.out.println("Potential error at line " + match.getEndLine() + ", column " + match.getColumn()
                        + ": " + match.getMessage());
                System.out.println("Suggested correction: " + match.getSuggestedReplacements());
                System.out.println("Short Message: " + match.getShortMessage());
                String replacements = "";
                if (match.getShortMessage().equals("Spelling mistake")) {
                    replacements += match.getSuggestedReplacements().get(0);
                } else {
                    for (String replacement : match.getSuggestedReplacements()) {
                        replacements += replacement;
                    }
                }
                text = text.substring(0, match.getFromPos() + offset) + replacements
                        + text.substring(match.getToPos() + offset, text.length());
                offset += replacements.length() - (match.getToPos() - match.getFromPos());
            }
            System.out.println("Final text:\n" + text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
