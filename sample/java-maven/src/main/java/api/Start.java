package api;

import hu.bongyor.cli.annotation.CliClass;
import hu.bongyor.cli.annotation.CliFunction;
import hu.bongyor.cli.annotation.CliParam;

@CliClass(
        shortcut = "d",
        description = "Default class"
)
public class Start {
    @CliClass(
            shortcut = "d",
            description = "Default class"
    )
    public static class DefaultClass{
        @CliParam(
                shortcut = "p",
                description = "Sample string parameter"
        )
        private String parameter = "default value";
        @CliFunction(
                shortcut = "d",
                description = "Default function"
        )
        public void defaultFunction() {
            System.out.println(parameter);
        }
    }
}
