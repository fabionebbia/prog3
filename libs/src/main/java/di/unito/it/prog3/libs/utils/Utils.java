package di.unito.it.prog3.libs.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

    public static String toUpperFirst(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    public static String toTitleCase(String str, String separator) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Arrays
                .stream(str.split(separator))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0))
                                + word.substring(1).toLowerCase())
                .collect(Collectors.joining(separator));
    }

    public static final String toTitleCase(String str) {
        return toTitleCase(str, " ");
    }

    public static <E extends Enum<E>> String toTitleCase(E e) {
        return toTitleCase(e.name(), "_").replaceAll(" +", " ");
    }


    public static String toCamelCase(Enum<?> e) {
        StringBuilder sb = new StringBuilder();

        String[] tokens = e.name().toLowerCase().split("_+");
        sb.append(tokens[0]);

        for (int i = 1; i < tokens.length; i++) {
            String capitalizedToken = tokens[i]
                    .substring(0, 1)
                    .toUpperCase()
                    .concat(tokens[i].substring(1));
            sb.append(capitalizedToken);
        }

        return sb.toString();
    }

   /* public static void bindVisibility(ObservableBooleanValue visibleManagedCondition,
                                      Node node,
                                      Node... others) {
        bindVisibility(visibleManagedCondition, null, node, others);
    }

    public static void bindVisibility(ObservableBooleanValue visibleManagedCondition,
                                      ObservableBooleanValue disableCondition,
                                      Node node,
                                      Node... others) {
        List<Node> nodes = Arrays.asList(others);
        nodes.add(node);
        if (others.length == 0) {

        }
        //bindVisibility(visibleManagedCondition, disableCondition, nodes);
    }*/

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

    public static void setVisibility(boolean condition, Node... nodes) {
        for (Node node : nodes) {
            node.managedProperty().set(condition);
            node.visibleProperty().set(condition);
            node.disableProperty().set(!condition);
        }
    }

}
