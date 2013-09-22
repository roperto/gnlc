@echo on
set JAVABIN=C:\Program Files\Java\jdk1.7.0_09\bin
set PUTTY=C:\Program Files (x86)\PuTTY

@echo PRE-CLEAN
cd ..
@IF NOT %ERRORLEVEL%==0 GOTO END
rmdir build /s /q
@IF NOT %ERRORLEVEL%==0 GOTO END
mkdir build
@IF NOT %ERRORLEVEL%==0 GOTO END
cd build
@IF NOT %ERRORLEVEL%==0 GOTO END

@echo listing files
dir ..\project\src\*.java /b /s >> files.txt
IF NOT %ERRORLEVEL%==0 GOTO END
@echo copy rxtx...
mkdir rxtx-lib
xcopy /e ..\project\rxtx-lib rxtx-lib
@IF NOT %ERRORLEVEL%==0 GOTO END
@echo copy resources...
@IF NOT %ERRORLEVEL%==0 GOTO END
mkdir res
xcopy /e ..\project\res res
@IF NOT %ERRORLEVEL%==0 GOTO END
@echo javac...
"%JAVABIN%\javac.exe" -d . -cp .;rxtx-lib/RXTXcomm.jar @files.txt
@IF NOT %ERRORLEVEL%==0 GOTO END
del files.txt
@IF NOT %ERRORLEVEL%==0 GOTO END

@echo create batch...
echo start javaw -cp .;./res/;rxtx-lib/RXTXcomm.jar net.geral.slotcar.lapcounter.LapCounter > gnlc.bat
@IF NOT %ERRORLEVEL%==0 GOTO END

@echo prepare zip
mkdir gnlc
@IF NOT %ERRORLEVEL%==0 GOTO END
move rxtx-lib gnlc
@IF NOT %ERRORLEVEL%==0 GOTO END
move gnlc.bat gnlc
@IF NOT %ERRORLEVEL%==0 GOTO END
move net gnlc
@IF NOT %ERRORLEVEL%==0 GOTO END
move res gnlc
@IF NOT %ERRORLEVEL%==0 GOTO END

@echo create jar (as zip)
"%JAVABIN%\jar.exe" cMf gnlc.zip gnlc

@echo COMPLETED!
:END
@pause
