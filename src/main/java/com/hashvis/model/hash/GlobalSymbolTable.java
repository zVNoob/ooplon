package com.hashvis.model.hash;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry of every built-in symbol (command name) understood by the
 * hash-visualizer DSL interpreter.
 *
 * <h2>Role in the architecture</h2>
 * <p>The DSL interpreter (hosted in the control-panel layer, P5) tokenises each
 * line the user types into a <em>command</em> word and zero or more
 * <em>argument</em> tokens. Before executing a command it calls
 * {@link #lookup(String)} to retrieve the corresponding {@link BuiltinCommand}
 * descriptor.  If the name is unknown the interpreter can report a meaningful
 * error to the user instead of throwing a raw exception.
 *
 * <h2>Built-in command catalogue</h2>
 * <p>Each entry in the table has:
 * <ul>
 *   <li>a canonical lower-case name (the symbol as typed by the user)</li>
 *   <li>a {@link BuiltinCommand} that records arity, short syntax summary, and
 *       a one-line description displayed in the built-in reference panel.</li>
 * </ul>
 *
 * <h2>Supported commands</h2>
 * <pre>
 *  insert  &lt;key&gt;          – insert a key into the hash table
 *  search  &lt;key&gt;          – search for a key (highlights probe path)
 *  delete  &lt;key&gt;          – delete a key (ghosts the item in open-addressing)
 *  reset                   – clear the whole table back to initial state
 *  size                    – print the current number of stored keys
 *  capacity                – print the table capacity (number of buckets)
 *  contains &lt;key&gt;         – print true/false without modifying visualizer state
 *  hash    &lt;key&gt;          – print the raw hash index for a key (no probing)
 *  help    [command]       – list all builtins or describe a single command
 *  clear                   – alias for reset (clears table and output log)
 *  load    &lt;filename&gt;     – execute a script file line by line
 *  echo    &lt;text…&gt;        – print arbitrary text to the output log
 *  pause   &lt;ms&gt;           – pause execution for &lt;ms&gt; milliseconds
 *  step                    – advance one visualizer step (used in scripts)
 *  setsize &lt;n&gt;            – resize the table to n buckets and reset
 * </pre>
 *
 * <h2>Thread safety</h2>
 * <p>The internal map is populated once at class-loading time and is
 * thereafter read-only, so {@link #lookup(String)} is safe to call from
 * any thread without synchronisation.
 */
public class GlobalSymbolTable {

  // -------------------------------------------------------------------------
  // Public inner type
  // -------------------------------------------------------------------------

  /**
   * Descriptor for a single built-in DSL command.
   *
   * <p>Instances are immutable value objects; they describe the command but do
   * not execute it – execution logic lives in the interpreter.
   */
  public static final class BuiltinCommand {

    /** Canonical (lower-case) name of the command. */
    private final String name;

    /**
     * Number of arguments the command expects.
     * {@code -1} means "variadic" (zero or more arguments).
     */
    private final int arity;

    /** Short usage synopsis shown in error messages, e.g. {@code "insert <key>"}. */
    private final String syntax;

    /** One-line description shown by the {@code help} command. */
    private final String description;

    /**
     * Creates a new command descriptor.
     *
     * @param name        canonical command name (lower-case)
     * @param arity       expected argument count, or {@code -1} for variadic
     * @param syntax      short usage synopsis
     * @param description one-line description for the help panel
     */
    public BuiltinCommand(String name, int arity, String syntax, String description) {
      this.name = name;
      this.arity = arity;
      this.syntax = syntax;
      this.description = description;
    }

    /** @return canonical command name */
    public String getName() { return name; }

    /**
     * @return expected argument count, or {@code -1} for variadic (0 or more)
     */
    public int getArity() { return arity; }

    /** @return short usage synopsis, e.g. {@code "insert <key>"} */
    public String getSyntax() { return syntax; }

    /** @return one-line description for the help reference panel */
    public String getDescription() { return description; }

    /**
     * Returns {@code true} if this command accepts a variable number of
     * arguments (arity == {@code -1}).
     *
     * @return {@code true} for variadic commands
     */
    public boolean isVariadic() { return arity == -1; }

    @Override
    public String toString() {
      return syntax + "  –  " + description;
    }
  }

  // -------------------------------------------------------------------------
  // Singleton / static registry
  // -------------------------------------------------------------------------

  /** Immutable map from lower-case command name to its descriptor. */
  private static final Map<String, BuiltinCommand> SYMBOLS;

  static {
    Map<String, BuiltinCommand> map = new HashMap<>();

    // ---- Core table operations -------------------------------------------

    register(map, new BuiltinCommand(
        "insert", 1,
        "insert <key>",
        "Insert <key> into the hash table and animate the probe sequence."));

    register(map, new BuiltinCommand(
        "search", 1,
        "search <key>",
        "Search for <key> and highlight each slot visited during probing."));

    register(map, new BuiltinCommand(
        "delete", 1,
        "delete <key>",
        "Delete <key> from the table; marks the slot as GHOSTED in open-addressing."));

    // ---- Table introspection ---------------------------------------------

    register(map, new BuiltinCommand(
        "size", 0,
        "size",
        "Print the number of keys currently stored in the table."));

    register(map, new BuiltinCommand(
        "capacity", 0,
        "capacity",
        "Print the number of buckets (slots) in the table."));

    register(map, new BuiltinCommand(
        "contains", 1,
        "contains <key>",
        "Print 'true' or 'false' – does not alter visualizer highlight state."));

    register(map, new BuiltinCommand(
        "hash", 1,
        "hash <key>",
        "Print the raw bucket index for <key> without probing or highlighting."));

    // ---- Table mutation --------------------------------------------------

    register(map, new BuiltinCommand(
        "reset", 0,
        "reset",
        "Remove all keys and reset every slot to NORMAL state."));

    register(map, new BuiltinCommand(
        "clear", 0,
        "clear",
        "Alias for 'reset': clear the table and the output log."));

    register(map, new BuiltinCommand(
        "setsize", 1,
        "setsize <n>",
        "Resize the table to <n> buckets, then reset all data."));

    // ---- Script / automation --------------------------------------------

    register(map, new BuiltinCommand(
        "load", 1,
        "load <filename>",
        "Execute every line of <filename> as DSL commands."));

    register(map, new BuiltinCommand(
        "step", 0,
        "step",
        "Advance the visualizer by one animation step (useful in scripts)."));

    register(map, new BuiltinCommand(
        "pause", 1,
        "pause <ms>",
        "Pause script execution for <ms> milliseconds before the next command."));

    // ---- Output / utility -----------------------------------------------

    register(map, new BuiltinCommand(
        "echo", -1,
        "echo <text...>",
        "Print the remaining tokens as a line to the output log."));

    register(map, new BuiltinCommand(
        "help", -1,
        "help [command]",
        "List all built-in commands, or describe a specific command if given."));

    SYMBOLS = Collections.unmodifiableMap(map);
  }

  /** Adds a command to the map, keyed by its lower-case name. */
  private static void register(Map<String, BuiltinCommand> map, BuiltinCommand cmd) {
    map.put(cmd.getName().toLowerCase(), cmd);
  }

  // -------------------------------------------------------------------------
  // Constructor – instances allowed but all state is static
  // -------------------------------------------------------------------------

  /**
   * Creates a GlobalSymbolTable instance.
   * All data is held in a static registry; instantiation is supported to allow
   * dependency injection in tests and the interpreter factory.
   */
  public GlobalSymbolTable() {
    // nothing to initialise – registry is static
  }

  // -------------------------------------------------------------------------
  // Public API
  // -------------------------------------------------------------------------

  /**
   * Looks up the built-in descriptor for a command name.
   *
   * <p>The lookup is case-insensitive: {@code "INSERT"}, {@code "Insert"}, and
   * {@code "insert"} all resolve to the same entry.
   *
   * @param name command token from the DSL tokeniser (must not be null)
   * @return the {@link BuiltinCommand} descriptor, or {@code null} if the name
   *         is not a registered built-in
   * @throws NullPointerException if {@code name} is null
   */
  public BuiltinCommand lookup(String name) {
    if (name == null) throw new NullPointerException("Command name must not be null");
    return SYMBOLS.get(name.toLowerCase());
  }

  /**
   * Returns {@code true} if the given name is a registered built-in command.
   *
   * @param name command token (case-insensitive)
   * @return {@code true} if registered
   */
  public boolean isDefined(String name) {
    return lookup(name) != null;
  }

  /**
   * Returns an unmodifiable view of all registered command names.
   *
   * <p>The names are lower-case canonical forms; the set order is undefined.
   *
   * @return immutable set of all built-in command names
   */
  public Set<String> getAllCommandNames() {
    return SYMBOLS.keySet(); // already unmodifiable
  }

  /**
   * Returns an unmodifiable view of the entire symbol registry.
   *
   * <p>Useful for building help-panel UIs that iterate over every command.
   *
   * @return immutable map from lower-case command name to {@link BuiltinCommand}
   */
  public Map<String, BuiltinCommand> getAllCommands() {
    return SYMBOLS;
  }

  /**
   * Generates a formatted multi-line help string listing every built-in
   * command with its syntax and description, sorted alphabetically.
   *
   * <p>This is the text emitted to the output log when the user types
   * {@code help} with no argument.
   *
   * @return formatted help listing
   */
  public String formatHelpAll() {
    StringBuilder sb = new StringBuilder();
    sb.append("Built-in commands:\n");
    sb.append("==================\n");

    SYMBOLS.keySet().stream().sorted().forEach(name -> {
      BuiltinCommand cmd = SYMBOLS.get(name);
      sb.append(String.format("  %-22s  %s%n", cmd.getSyntax(), cmd.getDescription()));
    });

    return sb.toString();
  }

  /**
   * Generates a formatted help string for a single named command.
   *
   * <p>This is the text emitted when the user types {@code help <command>}.
   *
   * @param name command name to describe (case-insensitive)
   * @return formatted help text, or an error string if the command is unknown
   */
  public String formatHelp(String name) {
    BuiltinCommand cmd = lookup(name);
    if (cmd == null)
      return "Unknown command: '" + name + "'. Type 'help' for a full list.";

    return String.format(
        "Command  : %s%n" +
        "Syntax   : %s%n" +
        "Arguments: %s%n" +
        "Info     : %s%n",
        cmd.getName(),
        cmd.getSyntax(),
        cmd.isVariadic() ? "variadic (0 or more)" : String.valueOf(cmd.getArity()),
        cmd.getDescription());
  }
}