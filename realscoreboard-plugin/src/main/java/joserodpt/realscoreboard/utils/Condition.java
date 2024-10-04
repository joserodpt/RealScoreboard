package joserodpt.realscoreboard.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Condition {
    private String condition; // Expresión condicional
    private List<String> result; // Resultados a devolver si la condición se cumple

    public Condition(String condition, List<String> result) {
        this.condition = condition;
        this.result = result;
    }
}
