package org.brutality.utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyUtils {

    /**
     * Converts a key name to its corresponding key code.
     *
     * @param keyName The name of the key.
     * @return The key code.
     */
    public static int getKeyCode(String keyName) {
        // Normalize the key name
        String keyNameUpper = keyName.toUpperCase();

        switch (keyNameUpper) {
            // Handle letters
            case "Q": return Keyboard.KEY_Q;
            case "W": return Keyboard.KEY_W;
            case "E": return Keyboard.KEY_E;
            case "R": return Keyboard.KEY_R;
            case "T": return Keyboard.KEY_T;
            case "Y": return Keyboard.KEY_Y;
            case "U": return Keyboard.KEY_U;
            case "I": return Keyboard.KEY_I;
            case "O": return Keyboard.KEY_O;
            case "P": return Keyboard.KEY_P;
            case "A": return Keyboard.KEY_A;
            case "S": return Keyboard.KEY_S;
            case "D": return Keyboard.KEY_D;
            case "F": return Keyboard.KEY_F;
            case "G": return Keyboard.KEY_G;
            case "H": return Keyboard.KEY_H;
            case "J": return Keyboard.KEY_J;
            case "K": return Keyboard.KEY_K;
            case "L": return Keyboard.KEY_L;
            case "Z": return Keyboard.KEY_Z;
            case "X": return Keyboard.KEY_X;
            case "C": return Keyboard.KEY_C;
            case "V": return Keyboard.KEY_V;
            case "B": return Keyboard.KEY_B;
            case "N": return Keyboard.KEY_N;
            case "M": return Keyboard.KEY_M;

            // Handle numbers
            case "0": return Keyboard.KEY_0;
            case "1": return Keyboard.KEY_1;
            case "2": return Keyboard.KEY_2;
            case "3": return Keyboard.KEY_3;
            case "4": return Keyboard.KEY_4;
            case "5": return Keyboard.KEY_5;
            case "6": return Keyboard.KEY_6;
            case "7": return Keyboard.KEY_7;
            case "8": return Keyboard.KEY_8;
            case "9": return Keyboard.KEY_9;

            // Handle function keys
            case "F1": return Keyboard.KEY_F1;
            case "F2": return Keyboard.KEY_F2;
            case "F3": return Keyboard.KEY_F3;
            case "F4": return Keyboard.KEY_F4;
            case "F5": return Keyboard.KEY_F5;
            case "F6": return Keyboard.KEY_F6;
            case "F7": return Keyboard.KEY_F7;
            case "F8": return Keyboard.KEY_F8;
            case "F9": return Keyboard.KEY_F9;
            case "F10": return Keyboard.KEY_F10;
            case "F11": return Keyboard.KEY_F11;
            case "F12": return Keyboard.KEY_F12;

            // Handle navigation and special keys
            case "SPACE": return Keyboard.KEY_SPACE;
            case "ENTER": return Keyboard.KEY_RETURN;
            case "ESCAPE": return Keyboard.KEY_ESCAPE;
            case "TAB": return Keyboard.KEY_TAB;
            case "CAPSLOCK": return Keyboard.KEY_CAPITAL;
            case "LSHIFT": return Keyboard.KEY_LSHIFT;
            case "RSHIFT": return Keyboard.KEY_RSHIFT;
            case "LCONTROL": return Keyboard.KEY_LCONTROL;
            case "RCONTROL": return Keyboard.KEY_RCONTROL;
            case "LALT": return Keyboard.KEY_LMENU;
            case "RALT": return Keyboard.KEY_RMENU;
            case "LEFT": return Keyboard.KEY_LEFT;
            case "RIGHT": return Keyboard.KEY_RIGHT;
            case "UP": return Keyboard.KEY_UP;
            case "DOWN": return Keyboard.KEY_DOWN;
            case "INSERT": return Keyboard.KEY_INSERT;
            case "DELETE": return Keyboard.KEY_DELETE;
            case "HOME": return Keyboard.KEY_HOME;
            case "END": return Keyboard.KEY_END;
            case "PAGEUP": return Keyboard.KEY_PRIOR;
            case "PAGEDOWN": return Keyboard.KEY_NEXT;
            case "NUMLOCK": return Keyboard.KEY_NUMLOCK;
            case "NUMPAD0": return Keyboard.KEY_NUMPAD0;
            case "NUMPAD1": return Keyboard.KEY_NUMPAD1;
            case "NUMPAD2": return Keyboard.KEY_NUMPAD2;
            case "NUMPAD3": return Keyboard.KEY_NUMPAD3;
            case "NUMPAD4": return Keyboard.KEY_NUMPAD4;
            case "NUMPAD5": return Keyboard.KEY_NUMPAD5;
            case "NUMPAD6": return Keyboard.KEY_NUMPAD6;
            case "NUMPAD7": return Keyboard.KEY_NUMPAD7;
            case "NUMPAD8": return Keyboard.KEY_NUMPAD8;
            case "NUMPAD9": return Keyboard.KEY_NUMPAD9;
            case "NUMPADADD": return Keyboard.KEY_ADD;
            case "NUMPADSUBTRACT": return Keyboard.KEY_SUBTRACT;
            case "NUMPADMULTIPLY": return Keyboard.KEY_MULTIPLY;
            case "NUMPADDIVIDE": return Keyboard.KEY_DIVIDE;

            // Handle mouse buttons
            case "MOUSE1": return 0; // Mouse button 1
            case "MOUSE2": return 1; // Mouse button 2
            case "MOUSE3": return 2; // Mouse button 3
            case "MOUSE4": return 3; // Mouse button 4
            case "MOUSE5": return 4; // Mouse button 5
            case "MOUSE6": return 5; // Mouse button 6
            case "MOUSE7": return 6; // Mouse button 7
            case "MOUSE8": return 7; // Mouse button 8

            // Default case for unknown keys
            default: return Keyboard.KEY_NONE;
        }
    }
}
