package MTSSynthesis.controller.model;

import MTSSynthesis.controller.ControllerSynthesisFacade;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.gr.perfect.StateSpaceCuttingPerfectInfoOppositeGrControlProblem;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSSimulationSemantics;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import com.google.common.collect.Sets;

import java.io.*;
import java.util.*;


public class ReportingPerfectInfoStateSpaceCuttingGRControlProblem
        extends StateSpaceCuttingPerfectInfoOppositeGrControlProblem {

    public class CompositeReport {
        public String testName;
        public int guaranteesCount;
        public int assumptionsCount;
        public int failuresCount;
        public boolean simulatesController;
        public boolean simulatedByController;
        public boolean isCompatible;

        public boolean isEquivalent() {
            return simulatesController && simulatedByController;
        }

        public List<CutterReport> cutterReports;

        public CompositeReport(String testName, ControllerGoal<String> goal,
                               List<CutterReport> cutterReports, LTS<Long, String> mut, LTS<Long, String> exp) {
            this(testName, goal, cutterReports, mut, exp, true);
        }


        public CompositeReport(String testName, ControllerGoal<String> goal,
                               List<CutterReport> cutterReports, LTS<Long, String> mut, LTS<Long, String> exp,
                               boolean checkCompatibility) {
            this.testName = testName;
            this.cutterReports = cutterReports;
            this.guaranteesCount = goal.getGuarantees().size();
            this.assumptionsCount = goal.getAssumptions().size();
            this.failuresCount = goal.getFaults().size();

            //removeUnusedActionsFromLTS(mut);
            //removeUnusedActionsFromLTS(exp);
            //we add missing actions from expected
            Set<String> expActions = exp.getActions();

            for (String action : mut.getActions())
                if (!expActions.contains(action))
                    exp.addAction(action);


            MTSAdapter<Long, String> mutAdapter = new MTSAdapter<Long, String>(mut);
            MTSAdapter<Long, String> expAdapter = new MTSAdapter<Long, String>(exp);


            LTSSimulationSemantics simulationSemantics = new LTSSimulationSemantics();
            /*
            simulatesController = simulationSemantics.isARefinement(mutAdapter,
					expAdapter);
			simulatedByController = simulationSemantics.isARefinement(
					expAdapter, mutAdapter);
			*/
            ControllerSynthesisFacade<Long, String, Integer> instance = new ControllerSynthesisFacade<Long, String, Integer>();
            if (checkCompatibility)
                this.isCompatible = instance.checkAssumptionsCompatibility(mutAdapter, goal);
            else
                this.isCompatible = true;
        }


        protected void removeUnusedActionsFromLTS(LTS<Long, String> machine) {
            Set<String> actionsToRemove = new HashSet<String>();
            // remove unused actions in order to pass equivalence check for the
            // result
            for (String action : machine.getActions()) {
                boolean hasAction = false;
                for (Long s : machine.getStates()) {
                    Set<Long> image = machine.getTransitions(s).getImage(action);
                    if (image != null && image != Collections.EMPTY_SET) {
                        if (image.size() > 0) {
                            hasAction = true;
                            break;
                        }
                    }
                }
                if (!hasAction)
                    actionsToRemove.add(action);
            }
            for (String actionToRemove : actionsToRemove)
                machine.removeAction(actionToRemove);
        }

        public String boolToString(boolean value) {
            return (value ? "Y" : "N");
        }

        public String toString() {
            return toString(false, false, "\t", "|", "\n", false);
        }

        public String getHeader() {
            return getHeader("\t", "", "\n", false);
        }

        public String getHeader(String columnSeparator, String sectionSeparator, String rowSeparator, boolean forLatex) {
            String cutsHeader = "";
            String cutsAlignment = "";
            if (forLatex) {
                cutsAlignment = "\\begin{tabular}{ | c | c c c c c c c | ";
                for (int i = 0; i < cutterReports.size(); i++)
                    cutsAlignment += "c c c c c | ";
                cutsAlignment += "}" + rowSeparator;
            }
            for (CutterReport report : cutterReports)
                cutsHeader += columnSeparator + sectionSeparator
                        + report.getHeader(columnSeparator, "", forLatex);
            if (forLatex) {
                return cutsAlignment + "\\rot{test name}" + columnSeparator + "\\rot{$c'=c$}" + columnSeparator
                        + "\\rot{$c'<c$}" + columnSeparator + "\\rot{$c'>c$}" + columnSeparator
                        + "\\rot{$|As|$}" + columnSeparator + "\\rot{$|G|$}" + columnSeparator + "\\rot{$|F|$}"
                        + columnSeparator + "\\rot{ass.Comp.}" + cutsHeader + rowSeparator;
            } else {
                return "test name" + columnSeparator + "c'=c" + columnSeparator
                        + "c'<c" + columnSeparator + "c'>c" + columnSeparator
                        + "#As" + columnSeparator + "#G" + columnSeparator + "#F"
                        + columnSeparator + "ass.comp." + cutsHeader + rowSeparator;
            }
        }

        public String getFooter() {
            return getFooter("\t", "\n", false);
        }

        public String getFooter(String columnSeparator, String rowSeparator, boolean forLatex) {
            if (forLatex) {
                return "\\end{tabular}" + rowSeparator;
            } else {
                return "";
            }

        }

        public String toString(boolean printHeader, boolean printFooter, String columnSeparator
                , String sectionSeparator, String rowSeparator, boolean forLatex) {
            String returnString = "";
            if (printHeader)
                returnString = getHeader(columnSeparator, sectionSeparator, rowSeparator, forLatex);
            String cutsString = "";
            for (CutterReport report : cutterReports)
                cutsString += columnSeparator
                        + report.toString(false, columnSeparator, "", forLatex);
            returnString = returnString + testName + columnSeparator
                    + boolToString(isEquivalent()) + columnSeparator
                    + boolToString(simulatesController) + columnSeparator
                    + boolToString(simulatedByController) + columnSeparator
                    + assumptionsCount + columnSeparator + guaranteesCount
                    + columnSeparator + failuresCount + columnSeparator + boolToString(isCompatible) + cutsString
                    + rowSeparator;
            if (printFooter)
                returnString += getFooter(columnSeparator, rowSeparator, forLatex);
            return returnString;
        }
    }

    public class CutterReport {
        public long initialTime;
        public long finishingTime;
        public String cutterName;
        public int initialStatesCount;
        public int finishingStatesCount;
        public boolean initialStateWinning;

        public CutterReport(String cutterName, int initialStatesCount) {
            this(cutterName, System.nanoTime(), System.nanoTime(), initialStatesCount,
                    initialStatesCount);
        }

        public CutterReport(String cutterName, long initialTime,
                            int initialStatesCount) {
            this(cutterName, initialTime, initialTime, initialStatesCount,
                    initialStatesCount);
        }

        public CutterReport(String cutterName, long initialTime,
                            long finishingTime, int initialStatesCount,
                            int finishingStatesCount) {
            this.cutterName = cutterName;
            this.initialTime = initialTime;
            this.initialStatesCount = initialStatesCount;
            this.finishingStatesCount = finishingStatesCount;
            this.finishingTime = System.nanoTime();
        }

        public int getStatesCountDelta() {
            return (finishingStatesCount - initialStatesCount);
        }

        public long getTimeDelta() {
            return (finishingTime - initialTime) / 1000000;
        }

        public String toString() {
            return toString(false, "\t", "\n", false);
        }

        public String getHeader(boolean forLatex) {
            return getHeader("\t", "\n", forLatex);
        }

        public String getHeader(String columnSeparator, String rowSeparator, boolean forLatex) {
            if (forLatex) {
                return "\\rot{cut name}" + columnSeparator + "\\rot{$|Long\\_0|$}" + columnSeparator
                        + "\\rot{$|Long\\_f|$" + columnSeparator + "\rot{$|S_d|$}" + columnSeparator
                        + "\rot{$\\exists C$}" + columnSeparator
                        + "\\rot{$T_d$}"
                        + rowSeparator;
            } else {
                return "cut name" + columnSeparator + "|S_0|" + columnSeparator
                        + "|S_f|" + columnSeparator + "|S_d|" + columnSeparator
                        + "hasCtrl" + columnSeparator
                        + "T_d"
                        + rowSeparator;
            }
        }


        public String toString(boolean printHeader, String columnSeparator,
                               String rowSeparator, boolean forLatex) {
            String returnString = "";
            if (printHeader)
                returnString = getHeader(columnSeparator, rowSeparator, forLatex);
            returnString = returnString + cutterName + columnSeparator
                    + initialStatesCount + columnSeparator
                    + finishingStatesCount + columnSeparator
                    + getStatesCountDelta() + columnSeparator
                    + (initialStateWinning ? "T" : "F") + columnSeparator
                    + getTimeDelta() + rowSeparator;
            return returnString;
        }
    }

    protected CompositeReport generalReport;

    protected LTS<Long, String> expectedResult;

    public CompositeReport getReport() {
        return generalReport;
    }


    public String getReportString(boolean hasHeader, boolean hasFooter, String columnSeparator,
                                  String sectionSeparator, String rowSeparator, boolean forLatex) {
        if (generalReport == null)
            return "";
        return generalReport.toString(hasHeader, hasFooter, columnSeparator, sectionSeparator, rowSeparator, forLatex);
    }

    protected String outputFileName;
    protected String testName;
    protected LTS<Long, String> envForController;

    protected boolean runNoG;
    protected boolean runAssumptions;
    protected boolean runAllNoG;
    protected boolean removeSafeStatesFirst;

    public ReportingPerfectInfoStateSpaceCuttingGRControlProblem(
            String testName,
            LTS<Long, String> originalEnvironment,
            LTS<Long, String> originalEnvironmentForController,
            ControllerGoal<String> controllerGoal, String outputFileName,
            LTS<Long, String> expectedResult,
            Long trapState,
            boolean runNoG,
            boolean relaxAllControllables,
            boolean relaxOnAssumptions,
            boolean relaxSelfLoops,
            boolean runAssumptions,
            boolean runAllNoG,
            boolean removeSafeStatesFirst) {
        super(originalEnvironment, controllerGoal, trapState, runNoG, runAssumptions, true, relaxAllControllables, relaxOnAssumptions, relaxSelfLoops);

        this.testName = testName;
        this.outputFileName = outputFileName;
        this.envForController = originalEnvironmentForController;
        this.expectedResult = expectedResult;

        this.runNoG = runNoG;
        this.runAssumptions = runAssumptions;
        this.runAllNoG = runAllNoG;
        this.removeSafeStatesFirst = removeSafeStatesFirst;
    }

    @Override
    protected LTS<Long, String> primitiveSolve() {
        List<CutterReport> reports = new ArrayList<CutterReport>();
        //ORIGINAL GR

        System.gc();
        GRGame<Long> game = new GRGameBuilder<Long, String>().buildGRGameFrom(new MTSAdapter<Long, String>(envForController), controllerGoal);
        GRRankSystem<Long> system = new GRRankSystem<Long>(game.getStates(), game.getGoal().getGuarantees(), game.getGoal().getAssumptions(), game.getGoal().getFailures());

        PerfectInfoGRGameSolver<Long> originalGameSolver = new PerfectInfoGRGameSolver<Long>(game, system);

        CutterReport originalSolverReport = new CutterReport("GR",
                environment.getStates().size());
        reports.add(originalSolverReport);

        originalGameSolver.solveGame();

        originalSolverReport.finishingTime = System.nanoTime();
        originalSolverReport.finishingStatesCount = originalGameSolver.getWinningStates().size();
        originalSolverReport.initialStateWinning = originalGameSolver.getWinningStates().containsAll(game.getInitialStates());
        System.gc();

        //WHOLE PROBLEM
        CutterReport grReport = new CutterReport("GR+CUT", environment
                .getStates().size());
        reports.add(grReport);

        //GR AFTER CUT
        CutterReport onlyGRAfterCutReport = new CutterReport("GR.only",
                environment.getStates().size());
        reports.add(onlyGRAfterCutReport);

        //OPPOSITE SAFE
        Set<Long> winningNoG = null;

        if (runNoG) {
            if (removeSafeStatesFirst) {
                SafeGameSolver<Long, String> safeGameSolver = new SafeGameSolver<Long, String>(environment, controllable);

                CutterReport safeReport = new CutterReport("Safe",
                        environment.getStates().size());
                reports.add(safeReport);

                safeGameSolver.solveGame();

                safeReport.finishingTime = System.nanoTime();

                System.gc();


                try {
                    File innerfile = new File(outputFileName + ".rep");
                    FileOutputStream innerfout = new FileOutputStream(innerfile);
                    // now convert the FileOutputStream into a PrintStream
                    PrintStream innerOutput = new PrintStream(innerfout);
                    innerOutput.close();
                    innerfout.close();
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                safeReport.finishingStatesCount = safeGameSolver.getWinningStates().size();
                safeReport.initialStateWinning = !(safeGameSolver.getWinningStates().containsAll(safeGameSolver.getGame().getInitialStates()));

                CutterReport safeRemoveReport = new CutterReport("SafeRemove",
                        environment.getStates().size());
                reports.add(safeRemoveReport);

                Set<Long> safeLosingStates = Sets.difference(environment.getStates(), safeGameSolver.getWinningStates());
                environment = removeInnerStates(safeLosingStates);


                safeRemoveReport.finishingTime = System.nanoTime();


                safeRemoveReport.finishingStatesCount = safeGameSolver.getWinningStates().size();
                safeRemoveReport.initialStateWinning = !(safeGameSolver.getWinningStates().containsAll(safeGameSolver.getGame().getInitialStates()));

            }

            OppositeSafeControlProblem safeControlProblem = new OppositeSafeControlProblem(environment, controllerGoal, trapState, relaxAllControllables, relaxOnAssumptions, relaxSelfLoops);

            safeControlProblem.getGameSolver();

            CutterReport oppositeSafeReport = new CutterReport("Opp.Safe",
                    environment.getStates().size());
            reports.add(oppositeSafeReport);

            safeControlProblem.solve();

            oppositeSafeReport.finishingTime = System.nanoTime();

            System.gc();

            winningNoG = safeControlProblem.getWinningStates();

//            System.out.println("TIME DELTA for safety on " + testName + ":" + oppositeSafeReport.getTimeDelta() + " size: " + winningNoG.size());
            try {
                File innerfile = new File(outputFileName + ".rep");
                FileOutputStream innerfout = new FileOutputStream(innerfile);
                // now convert the FileOutputStream into a PrintStream
                PrintStream innerOutput = new PrintStream(innerfout);
                innerOutput.println("TIME DELTA for safety on " + testName + ":" + oppositeSafeReport.getTimeDelta() + " size: " + winningNoG.size());
                innerOutput.close();
                innerfout.close();
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            oppositeSafeReport.finishingStatesCount = safeControlProblem.getWinningStates().size();
            oppositeSafeReport.initialStateWinning = !(safeControlProblem.getWinningStates().containsAll(safeControlProblem.getGameSolver().getGame().getInitialStates()));
            //return null;
        }


        //OPPOSITE GR
        Set<Long> winningAssumptions = null;

        if (runAssumptions) {
            PerfectInfoOppositeGRControlProblem<Long, String> oppositeGRControlProblem = new PerfectInfoOppositeGRControlProblem<Long, String>(environment, controllerGoal, trapState);

            CutterReport oppositeGRReport = new CutterReport("Opp.GR", environment
                    .getStates().size());
            reports.add(oppositeGRReport);

            oppositeGRControlProblem.solve();

            oppositeGRReport.finishingTime = System.nanoTime();

            winningAssumptions = oppositeGRControlProblem.getWinningStates();

            oppositeGRReport.finishingStatesCount = oppositeGRControlProblem.getWinningStates().size();
            oppositeGRReport.initialStateWinning = oppositeGRControlProblem.getWinningStates().containsAll(oppositeGRControlProblem.getGameSolver().getGame().getInitialStates());

        }

        Set<Long> losingStates = null;
        if (runAssumptions && runNoG) {
            CutterReport intersectionReport = new CutterReport("Intersection", environment
                    .getStates().size());
            reports.add(intersectionReport);

            losingStates = Sets.intersection(winningNoG, winningAssumptions);

            intersectionReport.finishingTime = System.nanoTime();
            intersectionReport.finishingStatesCount = losingStates.size();
        } else if (runAssumptions) {
            losingStates = Sets.difference(environment.getStates(), winningAssumptions);
        } else if (runNoG) {
            losingStates = winningNoG;
        }

        if (losingStates != null)
            environment = removeInnerStates(losingStates);


        //TODO:this should go elsewhere because we're doing it twice
        this.gameSolver = buildGameSolver();

        onlyGRAfterCutReport.initialTime = System.nanoTime();

        gameSolver.solveGame();

        grReport.finishingTime = System.nanoTime();
        grReport.finishingStatesCount = gameSolver.getWinningStates().size();
        onlyGRAfterCutReport.finishingTime = grReport.finishingTime;
        onlyGRAfterCutReport.finishingStatesCount = grReport.finishingStatesCount;
        onlyGRAfterCutReport.initialStateWinning = gameSolver.getWinningStates().containsAll(gameSolver.getGame().getInitialStates());

        System.gc();

        LTS<Long, String> result = buildStrategy();
        generalReport = new CompositeReport(testName, controllerGoal,
                reports, result, expectedResult);

        //LTS<Long, String> result = new LTSImpl<Long, String>(1L);


        generalReport = new CompositeReport(testName, controllerGoal,
                reports, result, expectedResult, true);

        try {
            LTSImpl<Long, String> resultImpl = (LTSImpl<Long, String>) result;

            String resourceFolder = new File(".").getCanonicalPath();
            File file = new File(resourceFolder + "/src/test/resources/" + outputFileName + ".lts");

            FileOutputStream fout = new FileOutputStream(file);
            // now convert the FileOutputStream into a PrintStream
            PrintStream myOutput = new PrintStream(fout);
            String resultString = resultImpl.toString();
            myOutput.print(resultString);
            myOutput.close();
            fout.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        PrintWriter writer;
        try {
            String resourceFolder = new File(".").getCanonicalPath();
            File fileOut = new File(resourceFolder + "/src/test/resources/" + outputFileName + ".lts");
            writer = new PrintWriter(fileOut, "UTF-8");
            writer.print(generalReport.toString(true, true, "&", "|", "\\\n", true));
            writer.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
}
