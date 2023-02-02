################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/CDCSerial.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/CPPLib.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/HardwareSerial.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/Print.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/Tone.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/VirtualSerial.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/WMath.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/WString.cpp \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring.cpp 

C_SRCS += \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/Descriptors.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/WInterrupts.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/pins_arduino.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_analog.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_digital.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_pulse.c \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_shift.c 

OBJS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/CDCSerial.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/CPPLib.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/Descriptors.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/HardwareSerial.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/Print.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/Tone.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/VirtualSerial.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/WInterrupts.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/WMath.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/WString.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/pins_arduino.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_analog.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_digital.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_pulse.o \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_shift.o 

C_DEPS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/Descriptors.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/WInterrupts.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/pins_arduino.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_analog.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_digital.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_pulse.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_shift.d 

CPP_DEPS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/CDCSerial.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/CPPLib.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/HardwareSerial.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/Print.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/Tone.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/VirtualSerial.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/WMath.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/WString.d \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring.d 


# Each subdirectory must supply rules for building sources it contributes
src/Link\ to\ DuinoBotKids.v1.xx_0022/CDCSerial.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/CDCSerial.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/CDCSerial.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/CDCSerial.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/CPPLib.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/CPPLib.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/CPPLib.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/CPPLib.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/Descriptors.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/Descriptors.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/Descriptors.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/Descriptors.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/HardwareSerial.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/HardwareSerial.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/HardwareSerial.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/HardwareSerial.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/Print.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/Print.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/Print.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/Print.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/Tone.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/Tone.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/Tone.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/Tone.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/VirtualSerial.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/VirtualSerial.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/VirtualSerial.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/VirtualSerial.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/WInterrupts.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/WInterrupts.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/WInterrupts.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/WInterrupts.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/WMath.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/WMath.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/WMath.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/WMath.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/WString.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/WString.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/WString.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/WString.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/pins_arduino.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/pins_arduino.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/pins_arduino.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/pins_arduino.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/wiring.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_analog.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_analog.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/wiring_analog.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_analog.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_digital.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_digital.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/wiring_digital.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_digital.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_pulse.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_pulse.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/wiring_pulse.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_pulse.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_shift.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_shift.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/wiring_shift.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/wiring_shift.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


