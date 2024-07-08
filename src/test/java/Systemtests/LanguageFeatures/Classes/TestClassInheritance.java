/* (C)2024 */
package Systemtests.LanguageFeatures.Classes;

import static Systemtests.TestHelpers.getProgramOutput;
import static Systemtests.TestHelpers.makeProgram;
import static org.junit.jupiter.api.Assertions.assertEquals;

import CBuilder.Expression;
import CBuilder.ProgramBuilder;
import CBuilder.Reference;
import CBuilder.literals.StringLiteral;
import CBuilder.objects.Call;
import CBuilder.objects.MPyClass;
import CBuilder.objects.SuperCall;
import CBuilder.objects.functions.Argument;
import CBuilder.objects.functions.Function;
import CBuilder.variables.Assignment;
import CBuilder.variables.VariableDeclaration;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestClassInheritance {
    String testClass = '[' + this.getClass().getSimpleName().toUpperCase() + "]\n";

    private static Stream<Arguments> sources_equals() {
        return Stream.of(
                Arguments.of(
                        new MPyClass(
                                "ClassA",
                                new Reference("__MPyType_Object"),
                                List.of(
                                        new Function(
                                                "__init__",
                                                List.of(
                                                        new SuperCall(List.of()),
                                                        new Call(
                                                                new Reference("print"),
                                                                List.of(
                                                                        new Expression[] {
                                                                            new StringLiteral(
                                                                                    "[ClassA] Print"
                                                                                        + " from"
                                                                                        + " __init__")
                                                                        }))),
                                                List.of(new Argument("self", 0)),
                                                List.of())),
                                Map.of()),
                        new MPyClass(
                                "ClassB",
                                new Reference("ClassA"),
                                List.of(
                                        new Function(
                                                "__init__",
                                                List.of(
                                                        new SuperCall(List.of()),
                                                        new Call(
                                                                new Reference("print"),
                                                                List.of(
                                                                        new Expression[] {
                                                                            new StringLiteral(
                                                                                    "[ClassB] Print"
                                                                                        + " from"
                                                                                        + " __init__")
                                                                        }))),
                                                List.of(new Argument("self", 0)),
                                                List.of())),
                                Map.of()),
                        """
                                [ClassA] Print from __init__
                                [ClassB] Print from __init__
                                """));
    }

    @ParameterizedTest
    @MethodSource("sources_equals")
    void inheritance(MPyClass A, MPyClass B, String expected, @TempDir Path workDirectory)
            throws IOException, InterruptedException {
        String result;

        generate_inheritance(workDirectory, A, B);

        makeProgram(workDirectory);

        result = getProgramOutput(workDirectory);

        System.out.println(testClass + " Result : " + result);

        // Thread.sleep(5000);
        assertEquals(expected, result);
    }

    /**
     * Mini Python source code for tests from top to bottom : <br>
     * <br>
     * class ClassA(__MPyType_Object): <br>
     * <br>
     * def __init__(self, val): <br>
     * super() <br>
     * print("[ClassA] Print from __init__\n") <br>
     * <br>
     * class ClassB(A): <br>
     * <br>
     * def __init__(self, val): <br>
     * super() <br>
     * print("[ClassB] Print from __init__\n") <br>
     * <br>
     * x = ClassB(123)
     */
    void generate_inheritance(Path output, MPyClass parent, MPyClass child) {
        ProgramBuilder builder = new ProgramBuilder();

        builder.addClass(parent);
        builder.addClass(child);

        builder.addVariable(new VariableDeclaration("x"));

        builder.addStatement(
                new Assignment(new Reference("x"), new Call(new Reference("ClassB"), List.of())));

        builder.writeProgram(output);
    }
}
