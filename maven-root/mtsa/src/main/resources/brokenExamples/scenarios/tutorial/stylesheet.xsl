<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="state">
<html>
  <head>
    <title>web application simulation</title>	
  </head>

  <body>

  <xsl:apply-templates select="header" />

  <form action="/" method="GET">
    <input type="hidden" name="role" value="{//state/env/role}" />
    <xsl:apply-templates select="transition" />
  </form>

  <xsl:apply-templates select="footer" />

  </body>
</html>
</xsl:template>


<xsl:template match="header">
</xsl:template>

<xsl:template match="footer">
</xsl:template>

<xsl:template match="transition">

  <xsl:choose>

    <!-- this template matches the different actions -->

    <xsl:when test="name='action1'">

	<!-- put html to be displayed when action1 is available here -->

    </xsl:when>

    <xsl:when test="name='action2'">

	<!-- put html to be displayed when action2 is available here -->

    </xsl:when>

    <!-- add more action cases here -->

    <xsl:when test="name='enterPassword'">

    <center>
    <table>

    <tr><td><input type="text" name="login" /></td>
        <td><input type="password" name="password" /></td>
        <td><input name="{number}" value="Login" type="submit" /></td>
    </tr> 

    </table>
    </center>

    </xsl:when>


    <xsl:when test="name='selectMsg'">

      <a href="/?role=user&amp;{number}=selectMsg">You have 1 new message.</a>

    </xsl:when>

  </xsl:choose>

</xsl:template>

</xsl:stylesheet>
