/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Apache.Beam.Dataflow.Java.app;

import Apache.Beam.Dataflow.Java.list.LinkedList;

import static Apache.Beam.Dataflow.Java.utilities.StringUtils.join;
import static Apache.Beam.Dataflow.Java.utilities.StringUtils.split;
import static Apache.Beam.Dataflow.Java.app.MessageUtils.getMessage;

import org.apache.commons.text.WordUtils;

public class App {
    public static void main(String[] args) {
        LinkedList tokens;
        tokens = split(getMessage());
        String result = join(tokens);
        System.out.println(WordUtils.capitalize(result));
    }
}

