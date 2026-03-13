################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.c 

OBJS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.o 

C_DEPS += \
./src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.d 


# Each subdirectory must supply rules for building sources it contributes
src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.o: ../src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/repos/robots/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"src/Link to DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.d" -MT"src/Link\ to\ DuinoBotKids.v1.xx_0022/LUFA/Drivers/USB/Class/Device/CDC.d" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


