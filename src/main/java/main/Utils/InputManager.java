package main.Utils;

import main.Given.Ticket;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class InputManager {
    public <T> T parseObject(T instance, Scanner scanner) {
        Stream<Method> setterArray = Stream.of(instance.getClass().getMethods())
                .filter(
                        e ->
                                e.getName().startsWith("set") &&
                                        e.getParameterCount() == 1 &&
                                        !Modifier.isStatic(e.getModifiers())
                )
                .sorted(Comparator.comparing(Method::getName));
        for (Object setter : setterArray.toArray()) {
            Method tmpSetter = (Method) setter;
            String tmpSetterName = tmpSetter.getName();
            Class<?> tmpFieldType = tmpSetter.getParameterTypes()[0];
            while (true) {
                try {
                    if (parseField(instance, tmpSetterName, tmpFieldType, scanner)) break;
                } catch (RuntimeException e) {
                    System.out.println("Unexpected EROR, please try again later");
                    return null;
                }
            }
        }
        return instance;
    }

    private boolean parseField(Object instance, String setterName, Class<?> fieldType, Scanner scanner) {
        Object result = null;
        String parsedInput = "";
        Method setterMethod;
        try {
            setterMethod = instance.getClass().getMethod(setterName, fieldType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected EROR");
        }
        String fieldName = Character.toLowerCase(setterName.charAt(3)) + setterName.substring(4);
        System.out.print("Insert value for field " + fieldName + " with type " + fieldType.getSimpleName());
        String output = "\n" + instance.getClass().getSimpleName() + " " + fieldName + ": ";

        try {
            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                System.out.print(output);
                parsedInput = scanner.nextLine();
                result = Integer.valueOf(parsedInput.trim());
            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                System.out.print(output);
                parsedInput = scanner.nextLine();
                result = Long.valueOf(parsedInput.trim());
            } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                System.out.print(output);
                parsedInput = scanner.nextLine();
                result = Double.valueOf(parsedInput.trim());
            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                System.out.print(output);
                parsedInput = scanner.nextLine();
                result = Float.valueOf(parsedInput.trim());
            } else if (fieldType.equals(String.class)) {
                System.out.print(output);
                parsedInput = scanner.nextLine();
                result = parsedInput.trim();
            } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                System.out.print(output);
                parsedInput = scanner.nextLine().trim();
                if (parsedInput.equals("false") || parsedInput.equals("true")) result = Boolean.valueOf(parsedInput);
                else {
                    System.out.printf("Cannot properly parse string '" + parsedInput +"', the value must be of type '" + fieldType.getSimpleName() + "'. Please try again");
                    return false;
                }
            } else if (fieldType.equals(ZonedDateTime.class)) {
                System.out.print(" (ISO format, e.g., 2023-10-05T14:30:00+03:00)");
                System.out.print(output);
                parsedInput = scanner.nextLine();
                result = ZonedDateTime.parse(parsedInput.trim());
            } else if (fieldType.isEnum()) {
                System.out.print(", possible values for this field: ");
                Object[] enumConstants = fieldType.getEnumConstants();
                ArrayList<String> enumConstantNames = new ArrayList<>();
                for (Object constant : enumConstants) {
                    Enum<?> value = (Enum<?>) constant;
                    enumConstantNames.add(value.name());
                }
                System.out.print(String.join(", ", enumConstantNames));
                System.out.print(output);
                parsedInput = scanner.nextLine().trim();
                if (!parsedInput.isEmpty()) result = Enum.valueOf((Class<Enum>) fieldType, parsedInput);
            }
        } catch (IllegalArgumentException | InputMismatchException | DateTimeParseException e) {
            System.out.printf("Cannot properly parse string '" + parsedInput +"', the value must be of type '" + fieldType.getSimpleName() + "'. Please try again");
            return false;
        }
        if (result != null || fieldType.isEnum()) {
            if (fieldType.equals(String.class) && ((String) result).isEmpty()) {
                result = null;
            }
            try {
                setterMethod.invoke(instance, result);
                return true;
            } catch (InvocationTargetException | IllegalAccessException e) {
                System.out.print(e.getCause().getMessage() + ", please try again");
                return false;
            }
        }

        System.out.println(" - this is an object, you will be prompted to insert its fields now");
        Object tmpObject;
        try {
            tmpObject = fieldType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            try {
                tmpObject = fieldType.getDeclaredConstructor(long.class).newInstance(((Ticket) instance).getId());
            } catch (Exception ex) {
                throw new RuntimeException("Unexpected EROR");
            }
        }
        parseObject(tmpObject, scanner);
        try {
            setterMethod.invoke(instance, tmpObject);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.print(e.getCause().getMessage() + ", please try again");
            return false;
        }
    }
}
