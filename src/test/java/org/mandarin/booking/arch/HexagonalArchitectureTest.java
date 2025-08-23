package org.mandarin.booking.arch;


import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "org.mandarin.booking", importOptions = ImportOption.DoNotIncludeTests.class)
public class HexagonalArchitectureTest {

    @ArchTest
    void hexagonalArchitectureTest(JavaClasses classes) {
        Architectures
                .layeredArchitecture()
                .consideringAllDependencies()
                .layer("adapter").definedBy("..adapter..")
                .layer("application").definedBy("..app..")
                .layer("domain").definedBy("..domain..")
                .whereLayer("adapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("application").mayOnlyBeAccessedByLayers("adapter")
                .whereLayer("domain").mayOnlyBeAccessedByLayers("adapter", "application")
                .check(classes);
    }
}
