package org.mandarin.booking.arch;


import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.mandarin.booking.arch.ModuleDependencyRulesTest.Module.APPLICATION;
import static org.mandarin.booking.arch.ModuleDependencyRulesTest.Module.COMMON;
import static org.mandarin.booking.arch.ModuleDependencyRulesTest.Module.DOMAIN;
import static org.mandarin.booking.arch.ModuleDependencyRulesTest.Module.EXTERNAL;
import static org.mandarin.booking.arch.ModuleDependencyRulesTest.Module.INTERNAL;
import static org.mandarin.booking.arch.ModuleDependencyRulesTest.Module.packages;

import org.junit.jupiter.api.Test;

class ModuleDependencyRulesTest extends BaseArchitectureTest {

    @Test
    void commonModuleShouldPure() {
        noClasses().that().resideInAnyPackage(BASE + ".common..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE + ".application..",
                        BASE + ".domain..",
                        BASE + ".internal..",
                        BASE + ".external.."
                )
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    void onlyApplicationMayDependOnDomain() {
        noClasses()
                .that().resideInAnyPackage(
                        packages(COMMON, INTERNAL, EXTERNAL)
                )
                .should().dependOnClassesThat().resideInAnyPackage(packages(APPLICATION, DOMAIN))
                .because("도메인 영역은 오직 application 계층에서만 의존 가능해야 한다")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    void domainShouldDependOnlyOnCommonAmongProjectModules() {
        noClasses()
                .that().resideInAnyPackage(packages(DOMAIN))
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        packages(APPLICATION,
                                INTERNAL,
                                EXTERNAL)
                )
                .because("domain은 프로젝트 내부 모듈 중 common만 의존할 수 있다")
                .allowEmptyShould(true)
                .check(classes);
    }

    enum Module {
        APPLICATION,
        COMMON,
        INTERNAL,
        EXTERNAL,
        DOMAIN;

        @Override
        public String toString() {
            return BASE + "." + name().toLowerCase();
        }

        static String[] packages(Module... modules) {
            String[] packages = new String[modules.length];
            for (int i = 0; i < modules.length; i++) {
                packages[i] = modules[i].toString() + "..";
            }
            return packages;
        }

    }
}
