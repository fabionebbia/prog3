package di.unito.it.prog3.libs.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

    public static final boolean debugEnabled = Boolean.parseBoolean(System.getProperty("debug"));


    public static void DEBUG(String message) {
        if (debugEnabled) System.out.println(message);
    }


    public static void bindVisibility(ObservableBooleanValue visibleManagedCondition, Node... nodes) {
        bindVisibility(visibleManagedCondition, null, nodes);
    }


    public static void bindVisibility(ObservableBooleanValue visibleManagedCondition,
                                      ObservableBooleanValue disableCondition,
                                      Node... nodes) {
        if (nodes.length == 0) throw new IllegalArgumentException("Need to specify at least one none");

        BooleanBinding disableBinding = Bindings.not(visibleManagedCondition);
        if (disableCondition != null) {
            disableBinding = disableBinding.or(Bindings.not(disableCondition));
        }

        for (Node node : nodes) {
            node.managedProperty().bind(visibleManagedCondition); // removes the node from the parent's layout calculation
            node.visibleProperty().bind(visibleManagedCondition);
            node.disableProperty().bind(disableBinding);
        }
    }


    public static String join(Collection<String> strings, String separator) {
        String[] tokens = strings.toArray(new String[0]);
        StringBuilder sb = new StringBuilder();
        int n = strings.size();

        for (int i = 0; i < n; i++) {
            sb.append(tokens[i]);
            if (i < n - 1) {
                sb.append(separator);
            }
        }

        return sb.toString();
    }
}
