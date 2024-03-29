package deftrainer.example.definitionstrainer.model;
/*
 * Copyright (C) 2013-2015 Juha Kuitunen
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Stack;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.util.Log;

/**
 * Implements support for ordered and unordered lists in to Android TextView.
 * Some code taken from inner class android.text.Html.HtmlToSpannedConverter. If you find this code useful,
 * please vote my answer at <a href="http://stackoverflow.com/a/17365740/262462">StackOverflow</a> up.
 */
public class MyTagHandler implements Html.TagHandler {
    /**
     * Keeps track of lists (ol, ul). On bottom of Stack is the outermost list
     * and on top of Stack is the most nested list
     */
    Stack<String> lists = new Stack<String>();
    /**
     * Tracks indexes of ordered lists so that after a nested list ends
     * we can continue with correct index of outer list
     */
    Stack<Integer> olNextIndex = new Stack<Integer>();

    Stack<Integer> last_character_width = new Stack<>();
    private static final String[][]counters =
            {{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"},
             {"1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10."},
             {"a)", "b)", "c)", "d)", "e)", "f)", "g)", "h)", "i)", "j)"},
             {"i.", "ii.", "iii.", "iv.", "v.", "vi.", "vii.", "viii.", "ix.", "x."},
             {"1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10."},
             {"a.", "b.", "c.", "d.", "e.", "f.", "g.", "h.", "i.", "j."}};
    /**
     * List indentation in pixels. Nested lists use multiple of this.
     */
    private static final int INDENT = 20;
    private static final int LIST_ITEM_INDENT = INDENT * 2 ;
    private static final BulletSpan BULLET = new BulletSpan(INDENT);
    private int[] derp = {30, 0, 0, 0, 0, 0};
    private int depth = -1;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("ul")) {
            if (opening) {
                lists.push(tag);
            } else {
                lists.pop();
            }
        } else if (tag.equalsIgnoreCase("ol")) {
            if (opening) {
                lists.push(tag);
                olNextIndex.push(1);//TODO: add support for lists starting other index than 1
                depth++;
            } else {
                lists.pop();
                olNextIndex.pop();
                depth--;
            }
        } else if (tag.equalsIgnoreCase("lx")) {
            if (opening) {
                if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                    output.append("\n");
                }
                String parentList = lists.peek();
                if (parentList.equalsIgnoreCase("ol")) {
                    start(output, new Ol());
                    String counter = (counters[lists.size() - 1][olNextIndex.peek() - 1]) + " ";
                    output.append(counter);
                    olNextIndex.push(olNextIndex.pop() + 1);

                } else if (parentList.equalsIgnoreCase("ul")) {
                    start(output, new Ul());
                }
            } else {
                if (lists.peek().equalsIgnoreCase("ul")) {
                    if ( output.length() > 0 && output.charAt(output.length() - 1) != '\n' ) {
                        output.append("\n");
                    }
                    // Nested BulletSpans increases distance between bullet and text, so we must prevent it.
                    int bulletMargin = INDENT;
                    if (lists.size() > 1) {
                        bulletMargin = INDENT - BULLET.getLeadingMargin(true);
                        if (lists.size() > 2) {
                            // This get's more complicated when we add a LeadingMarginSpan into the same line:
                            // we have also counter it's effect to BulletSpan
                            bulletMargin -= (lists.size() - 2) * LIST_ITEM_INDENT;
                        }
                    }
                    BulletSpan newBullet = new BulletSpan(bulletMargin);
                    end(output,
                            Ul.class,
                            new LeadingMarginSpan.Standard(LIST_ITEM_INDENT * (lists.size() - 1)),
                            newBullet);
                } else if (lists.peek().equalsIgnoreCase("ol")) {
                    if ( output.length() > 0 && output.charAt(output.length() - 1) != '\n' ) {
                        output.append("\n");
                    }
                    int numberMargin = LIST_ITEM_INDENT * (lists.size() - 1);
                    if (lists.size() > 1) {
                        // Same as in ordered lists: counter the effect of nested Spans
                        numberMargin -= (lists.size() - 2) * LIST_ITEM_INDENT;
                    }
                    end(output,
                            Ol.class,
                            new LeadingMarginSpan.Standard(numberMargin, numberMargin +derp[depth]));
                }
            }
        } else {
            if (opening) Log.d("TagHandler", "Found an unsupported tag " + tag);
        }
    }

    /** @see android.text.Html */
    private static void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
    }

    /** Modified from {@link android.text.Html} */
    private static void end(Editable text, Class<?> kind, Object... replaces) {
        int len = text.length();
        Object obj = getLast(text, kind);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        if (where != len) {
            for (Object replace: replaces) {
                text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return;
    }

    /** @see android.text.Html */
    private static Object getLast(Spanned text, Class<?> kind) {
        /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        }
        return objs[objs.length - 1];
    }

    private static int stackSumm(Stack<Integer> stack) {
        int summ = 0;
        for (int s : stack) {
            summ += s;
        }
        return summ;
    }

    private static class Ul { }
    private static class Ol { }

}
