package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.TaskBook;
import seedu.address.model.TaskBookModel;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskDescriptionContainsKeywordsPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.EditTaskDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_ROLE_HUSBAND = "husband";
    public static final String VALID_ROLE_FRIEND = "friend";
    public static final String VALID_TASK_DESCRIPTION = "Submit assignment";
    public static final String VALID_TASK_TYPE_T = "T";
    public static final String VALID_TASK_TYPE_D = "D";
    public static final String VALID_COMMENT = "Hard assignment";
    public static final String VALID_DATE = "10/10/2024";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_ADDRESS_AMY;
    public static final String ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOB;
    public static final String ROLE_DESC_FRIEND = " " + PREFIX_ROLE + VALID_ROLE_FRIEND;
    public static final String ROLE_DESC_HUSBAND = " " + PREFIX_ROLE + VALID_ROLE_HUSBAND;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_ADDRESS_DESC = " " + PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_ROLE_DESC = " " + PREFIX_ROLE + "hubby*"; // '*' not allowed in roles

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditPersonDescriptor DESC_AMY;
    public static final EditCommand.EditPersonDescriptor DESC_BOB;
    public static final EditTaskCommand.EditTaskDescriptor DESC_TASK_1;
    public static final EditTaskCommand.EditTaskDescriptor DESC_TASK_2;

    static {
        DESC_AMY = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withRoles(VALID_ROLE_FRIEND).build();
        DESC_BOB = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withRoles(VALID_ROLE_HUSBAND, VALID_ROLE_FRIEND).build();
        DESC_TASK_1 = new EditTaskDescriptorBuilder().withTaskDescription(VALID_TASK_DESCRIPTION)
                .withComment(VALID_COMMENT).withDate(VALID_DATE).withTaskType(VALID_TASK_TYPE_T).build();
        DESC_TASK_2 = new EditTaskDescriptorBuilder().withTaskDescription(VALID_TASK_DESCRIPTION)
                .withComment(VALID_COMMENT).withDate(VALID_DATE).withTaskType(VALID_TASK_TYPE_D).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel,
        TaskBookModel actualTaskBookModel, CommandResult expectedCommandResult,
        Model expectedModel, TaskBookModel expectedTaskBookModel) {
        try {
            CommandResult result = command.execute(actualModel, actualTaskBookModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
            assertEquals(expectedTaskBookModel, actualTaskBookModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel,
        TaskBookModel actualTaskBookModel, String expectedMessage,
        Model expectedModel, TaskBookModel expectedTaskBookModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, actualTaskBookModel, expectedCommandResult,
                    expectedModel, expectedTaskBookModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered person list and selected person in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel,
        TaskBookModel actualTaskBookModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        TaskBook expectedTaskBook = new TaskBook(actualTaskBookModel.getTaskBook());
        List<Person> expectedFilteredList = new ArrayList<>(actualModel.getFilteredPersonList());
        List<Task> expectedTaskFilteredList = new ArrayList<>(actualTaskBookModel.getFilteredTaskList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel, actualTaskBookModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedTaskBook, actualTaskBookModel.getTaskBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredPersonList());
        assertEquals(expectedTaskFilteredList, actualTaskBookModel.getFilteredTaskList());
    }


    /**
     * Updates {@code model}'s filtered list to show only the person at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showPersonAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredPersonList().size());

        Person person = model.getFilteredPersonList().get(targetIndex.getZeroBased());
        final String[] splitName = person.getName().fullName.split("\\s+");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredPersonList().size());
    }

    /**
     * Updates {@code model}'s filtered list to show only the task at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showTaskAtIndex(TaskBookModel taskBookModel, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < taskBookModel.getFilteredTaskList().size());

        Task task = taskBookModel.getFilteredTaskList().get(targetIndex.getZeroBased());
        final String[] splitName = task.getDescription().fullTaskDescription.split("\\s+");
        taskBookModel.updateFilteredTaskList(new TaskDescriptionContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, taskBookModel.getFilteredTaskList().size());
    }

}
