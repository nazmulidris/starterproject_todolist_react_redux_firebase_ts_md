package com.r3bl.todo_app.ui.reduxdebug;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nazmul on 11/28/16.
 */

public class StringComparisonUtil {

private static final String OLD_POSITION = "OLD_POSITION";
private static final String NEW_POSITION = "NEW_POSITION";
private static final int DEPTH_SAME_WORDS = 3;
private static final String ADDED_START_HTML_CLASS = "<p class=\"addedWords\">";
private static final String ADDED_END_HTML_CLASS = "</p>";
private static final String DELETED_START_HTML_CLASS = "<p class=\"deletedWords\">";
private static final String DELETED_END_HTML_CLASS = "</p>";
private static final String START_ADDED_TAG = "[<added>]";
private static final String END_ADDED_TAG = "[</added>]";
private static final String START_DELETED_TAG = "[<deleted>]";
private static final String END_DELETED_TAG = "[</deleted>]";

/**
 * Compares two strings.
 * Returns result string which combines old value with highlighted added and deleted words.
 * Added words are between [<added>] and [</added>].
 * Deleted words are between [<deleted>] and [</deleted>].
 * @param oldInstance
 * @param newInstance
 * @return differenece result
 */
public static final String getComparisonString(String oldInstance, String newInstance){
  String oldWords[] = oldInstance.split(" ");
  String newWords[] = newInstance.split(" ");
  StringBuilder result = new StringBuilder();
  doComparing(oldWords, newWords, result);
  return result.toString();
}

/**
 * Compares two strings.
 * Returns result string which combines old value with highlighted added and deleted words.
 * Added words are between <p class=\"addedWords\"> and </p>.
 * Deleted words are between <p class=\"deletedWords\"> and </p>.
 * @param oldInstance
 * @param newInstance
 * @return differenece result
 */
public static final String getComparisonStringHtmlDecoratedWithClasses(String oldInstance, String newInstance){
  String resultString = getComparisonString(oldInstance, newInstance);
  resultString = resultString.replace(START_ADDED_TAG,ADDED_START_HTML_CLASS);
  resultString = resultString.replace(END_ADDED_TAG,ADDED_END_HTML_CLASS);
  resultString = resultString.replace(START_DELETED_TAG,DELETED_START_HTML_CLASS);
  resultString = resultString.replace(END_DELETED_TAG,DELETED_END_HTML_CLASS);
  return resultString;
}


private static void doComparing(String[] oldWords, String[] newWords, StringBuilder result) {
  int newPosition = 0;
  for (int i = 0; i< oldWords.length; i++){
    if (isWordSame(oldWords, newWords, newPosition, i)){
      result.append(oldWords[i]);
      result.append(" ");
      newPosition++;
    }else{
      Map<String,Integer> resultPositions = resolveConflict(newPosition, i, newWords, oldWords, result);
      newPosition = resultPositions.get(NEW_POSITION) ;
      newPosition++;
      i = resultPositions.get(OLD_POSITION)-1;
    }
  }
}

private static boolean isWordSame(String[] oldWords, String[] newWords, int newPosition, int i) {
  return oldWords[i].equals(newWords[newPosition]);
}

private static Map<String,Integer> resolveConflict(int newWordPos, int oldWordPos, String[] newWords, String[] oldWords, StringBuilder result){
  Map<String,Integer> resultPositions = new HashMap<String,Integer>();
  int positionInOld = findNewWordInOldInstance(newWordPos, oldWordPos, newWords, oldWords);
  if (positionInOld == -1){
    result.append(START_ADDED_TAG);
    int newInstanceOldWordPosition = 0;
    int positionInOldFrom = 0;
    for (int j = newWordPos; j<newWords.length; j++){
      if (positionInOld == -1){
        positionInOld = findNewWordInOldInstance(newWordPos, oldWordPos, newWords, oldWords);
      }
      if (positionInOld > 0 ){
        positionInOldFrom = positionInOld;
        break;
      }
      result.append(newWords[newWordPos]);
      result.append(" ");
      newWordPos++;
      newInstanceOldWordPosition = j;
    }
    result.append(END_ADDED_TAG);
    appendDeletedWords(oldWordPos, oldWords, result, positionInOldFrom);
    resultPositions.put(NEW_POSITION, newInstanceOldWordPosition);
    resultPositions.put(OLD_POSITION, positionInOldFrom);
  }else{
    appendDeletedWords(oldWordPos, oldWords, result, positionInOld);
    resultPositions.put(NEW_POSITION, newWordPos-1);
    resultPositions.put(OLD_POSITION, positionInOld);
  }
  return resultPositions;
}


private static void appendDeletedWords(int oldWordPos, String[] oldWords, StringBuilder result, int positionInOld) {
  if (positionInOld - oldWordPos> 0){
    result.append(START_DELETED_TAG);
    for (int j = oldWordPos; j<positionInOld; j++){
      result.append(oldWords[j]);
      result.append(" ");
    }
    result.append(END_DELETED_TAG);
  }
}

private static int findNewWordInOldInstance(int newWordPos, int oldWordPos, String[] newWords, String[] oldWords) {
  for (int i = oldWordPos; i< oldWords.length; i++){
    if (newWords.length <= newWordPos){
      return i;
    }
    boolean found = false;
    for (int depth = 0; depth<DEPTH_SAME_WORDS; depth++){
      if (thereAreNoMoreWords(newWordPos+depth, newWords, oldWords, i+depth)){
        return i;
      }
      if (isWordSame(oldWords, newWords, newWordPos+depth, i+depth)){
        found = true;
        continue;
      }else{
        found = false;
        break;
      }
    }
    if (found){
      return i;
    }
  }
  return -1;
}

private static boolean thereAreNoMoreWords(int newWordPos, String[] newWords, String[] oldWords, int i) {
  return oldWords.length<=i || newWords.length <= newWordPos;
}


}