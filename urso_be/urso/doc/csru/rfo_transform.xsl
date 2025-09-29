<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zoz="http://www.egov.sk/mvsr/RFO/Podp/Ext/ZoznamIFOSoZmenenymiReferencnymiUdajmiWS-v1.0">
    <xsl:output method="xml" omit-xml-declaration="no" indent="yes" encoding="utf-8"/>

    <!-- Receives the id of the menu being rendered. -->
    <xsl:param name="currentTimestamp" />
    <xsl:param name="validFrom" />

    <xsl:template match="/">
        <POV>
            <KO><xsl:value-of select=" zoz:TransEnvOut/zoz:POV/zoz:KO"/></KO>
            <AC><xsl:value-of select=" zoz:TransEnvOut/zoz:POV/zoz:AC"/></AC>
            <OEXList>
                <xsl:for-each select=" zoz:TransEnvOut/zoz:POV/zoz:ZZVList/zoz:ZZV">
                <OEX>
                    <ID><xsl:value-of select="zoz:ID"/></ID>
                    <PO><xsl:value-of select="zoz:NI"/></PO>
                    <RC><xsl:value-of select="zoz:RC"/></RC>
					<xsl:choose>
						<xsl:when  test="zoz:DN != ''">
							<DN><xsl:value-of select="zoz:DN"/></DN>
						</xsl:when>
						<xsl:otherwise>
							<DN xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when  test="zoz:UC != ''">
							<UC><xsl:value-of select="zoz:UC"/></UC>
						</xsl:when>
						<xsl:otherwise>
							<UC xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <UCEUCNA><xsl:value-of select="zoz:UCEUCNA"/></UCEUCNA>
                    <MN><xsl:value-of select="zoz:MN"/></MN>
					<xsl:choose>
						<xsl:when  test="zoz:SN != ''">
							<SN><xsl:value-of select="zoz:SN"/></SN>
						</xsl:when>
						<xsl:otherwise>
							<SN xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <STASNNA><xsl:value-of select="zoz:STASNNA"/></STASNNA>
					<xsl:choose>
						<xsl:when  test="zoz:PO != ''">
							<PI><xsl:value-of select="zoz:PO"/></PI>
						</xsl:when>
						<xsl:otherwise>
							<PI xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <POHPINA><xsl:value-of select="zoz:POHPONA"/></POHPINA>
					<xsl:choose>
						<xsl:when  test="zoz:RS != ''">
							<RS><xsl:value-of select="zoz:RS"/></RS>
						</xsl:when>
						<xsl:otherwise>
							<RS xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <RSTRSNA><xsl:value-of select="zoz:RSTRSNA"/></RSTRSNA>
					<xsl:choose>
						<xsl:when  test="zoz:ND != ''">
							<NI><xsl:value-of select="zoz:ND"/></NI>
						</xsl:when>
						<xsl:otherwise>
							<NI xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <NARNINA><xsl:value-of select="zoz:NARNDNA"/></NARNINA>
					<xsl:choose>
						<xsl:when  test="zoz:DU != ''">
							<DU><xsl:value-of select="zoz:DU"/></DU>
						</xsl:when>
						<xsl:otherwise>
							<DU xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when  test="zoz:UL != ''">
							<UE><xsl:value-of select="zoz:UL"/></UE>
						</xsl:when>
						<xsl:otherwise>
							<UE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <UCEUENA><xsl:value-of select="zoz:UCEULNA"/></UCEUENA>
                    <MU><xsl:value-of select="zoz:MU"/></MU>
                    <UM><xsl:value-of select="zoz:UM"/></UM>
					<xsl:choose>
						<xsl:when  test="zoz:SU != ''">
							<SU><xsl:value-of select="zoz:SU"/></SU>
						</xsl:when>
						<xsl:otherwise>
							<SU xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <STASUNA><xsl:value-of select="zoz:STASUNA"/></STASUNA>
                    <BI><xsl:value-of select="zoz:BI"/></BI>
					<xsl:choose>
						<xsl:when  test="zoz:TV != ''">
							<TV><xsl:value-of select="zoz:TV"/></TV>
						</xsl:when>
						<xsl:otherwise>
							<TV xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <TVKTVNA><xsl:value-of select="zoz:TVKTVNA"/></TVKTVNA>
					<xsl:choose>
						<xsl:when  test="zoz:UE != ''">
							<UL><xsl:value-of select="zoz:UE"/></UL>
						</xsl:when>
						<xsl:otherwise>
							<UL xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <UCEULNA><xsl:value-of select="zoz:UCEUENA"/></UCEULNA>
					<xsl:choose>
						<xsl:when  test="zoz:UO != ''">
							<UO><xsl:value-of select="zoz:UO"/></UO>
						</xsl:when>
						<xsl:otherwise>
							<UO xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <UCEUONA><xsl:value-of select="zoz:UCEUONA"/></UCEUONA>
					<xsl:choose>
						<xsl:when  test="zoz:SZ != ''">
							<SZ><xsl:value-of select="zoz:SZ"/></SZ>
						</xsl:when>
						<xsl:otherwise>
							<SZ xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <SZVSZNA><xsl:value-of select="zoz:SZVSZNA"/></SZVSZNA>
					<xsl:choose>
						<xsl:when  test="zoz:RN != ''">
							<RN><xsl:value-of select="zoz:RN"/></RN>
						</xsl:when>
						<xsl:otherwise>
							<RN xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when  test="zoz:DK != ''">
							<DK><xsl:value-of select="zoz:DK"/></DK>
						</xsl:when>
						<xsl:otherwise>
							<DK xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <DL><xsl:value-of select="zoz:DT"/></DL>
					<xsl:choose>
						<xsl:when  test="zoz:KO != ''">
							<NK><xsl:value-of select="zoz:KO"/></NK>
						</xsl:when>
						<xsl:otherwise>
							<NK xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
					<DP><xsl:value-of select="zoz:NU"/></DP>
					<xsl:choose>
						<xsl:when  test="zoz:PG != ''">
							<PG><xsl:value-of select="zoz:PG"/></PG>
						</xsl:when>
						<xsl:otherwise>
							<PG xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</xsl:otherwise>
					</xsl:choose>
                    <MOSList>
                        <xsl:for-each select="zoz:ZMEList/zoz:ZME">
                        <MOS>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
                            <ME><xsl:value-of select="zoz:ME"/></ME>
                            <PO><xsl:value-of select="zoz:PA"/></PO>
                        </MOS>
                        </xsl:for-each>
                    </MOSList>
                    <PRIList>
                        <xsl:for-each select="zoz:ZPRList/zoz:ZPR">
                        <PRI>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
                            <PO><xsl:value-of select="zoz:PO"/></PO>
                            <PR><xsl:value-of select="zoz:PR"/></PR>
                        </PRI>
                        </xsl:for-each>
                    </PRIList>
                    <RPRList>
                        <xsl:for-each select="zoz:ZRPList/zoz:ZRP">
                        <RPR>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
                            <PO><xsl:value-of select="zoz:PO"/></PO>
                            <RP><xsl:value-of select="zoz:RP"/></RP>
                        </RPR>
                        </xsl:for-each>
                    </RPRList>
                    <TOSList>
                        <xsl:for-each select="zoz:ZTIList/zoz:ZTI">
                        <TOS>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
                            <TITTINA><xsl:value-of select="zoz:TITTINA"/></TITTINA>
                            <TTITTNA><xsl:value-of select="zoz:TTITTNA"/></TTITTNA>
							<xsl:choose>
								<xsl:when  test="zoz:TI != ''">
									<TI><xsl:value-of select="zoz:TI"/></TI>
								</xsl:when>
								<xsl:otherwise>
									<TI xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:TT != ''">
									<TT><xsl:value-of select="zoz:TT"/></TT>
								</xsl:when>
								<xsl:otherwise>
									<TT xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:FR != ''">
									<FR><xsl:value-of select="zoz:FR"/></FR>
								</xsl:when>
								<xsl:otherwise>
									<FR xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                        </TOS>
                        </xsl:for-each>
                    </TOSList>
                    <SPRList>
                        <xsl:for-each select="zoz:ZSPList/zoz:ZSP">
                        <SPR>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:choose>
								<xsl:when  test="zoz:STASINA != ''">
									<STASTNA><xsl:value-of select="zoz:STASINA"/></STASTNA>
								</xsl:when>
								<xsl:otherwise>
									<STASTNA xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<ST><xsl:value-of select="zoz:SI"/></ST>
                        </SPR>
                        </xsl:for-each>
                    </SPRList>
                    <RVEList>
                        <xsl:for-each select="zoz:ZVRList/zoz:ZVR">
                        <RVE>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
                            <IF><xsl:value-of select="zoz:ID"/></IF>
							<xsl:choose>
								<xsl:when  test="zoz:TR != ''">
									<TR><xsl:value-of select="zoz:TR"/></TR>
								</xsl:when>
								<xsl:otherwise>
									<TR xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>			
                            <TRZTRNA><xsl:value-of select="zoz:TRZTRNA"/></TRZTRNA>
							<xsl:choose>
								<xsl:when  test="zoz:DV != ''">
									<DV><xsl:value-of select="zoz:DV"/></DV>
								</xsl:when>
								<xsl:otherwise>
									<DV xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <MV><xsl:value-of select="zoz:MV"/></MV>
							<xsl:choose>
								<xsl:when  test="zoz:UC != ''">
									<UC><xsl:value-of select="zoz:UC"/></UC>
								</xsl:when>
								<xsl:otherwise>
									<UC xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <UCEUCNA><xsl:value-of select="zoz:UCEUCNA"/></UCEUCNA>
							<xsl:choose>
								<xsl:when  test="zoz:TO != ''">
									<TL><xsl:value-of select="zoz:TO"/></TL>
								</xsl:when>
								<xsl:otherwise>
									<TL xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <TRRTLNA><xsl:value-of select="zoz:TRRTONA"/></TRRTLNA>
							<xsl:choose>
								<xsl:when  test="zoz:TL != ''">
									<TE><xsl:value-of select="zoz:TL"/></TE>
								</xsl:when>
								<xsl:otherwise>
									<TE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <TRRTENA><xsl:value-of select="zoz:TRRTLNA"/></TRRTENA>
                            <SM><xsl:value-of select="zoz:SM"/></SM>
							<xsl:choose>
								<xsl:when  test="zoz:PA != ''">
									<PA><xsl:value-of select="zoz:PA"/></PA>
								</xsl:when>
								<xsl:otherwise>
									<PA xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                        </RVE>
                        </xsl:for-each>
                    </RVEList>
                    <POBList>
                        <xsl:for-each select="zoz:ZPBList/zoz:ZPB">
                        <POB>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:choose>
								<xsl:when  test="zoz:TP != ''">
									<TP><xsl:value-of select="zoz:TP"/></TP>
								</xsl:when>
								<xsl:otherwise>
									<TP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <TB><xsl:value-of select="zoz:TPOTPNA"/></TB><!--??? nieje uplne jasne-->
							<xsl:choose>
								<xsl:when  test="zoz:DP != ''">
									 <DP><xsl:value-of select="zoz:DP"/></DP>
								</xsl:when>
								<xsl:otherwise>
									<DP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>			
							<xsl:choose>
								<xsl:when  test="zoz:DK != ''">
									<DU><xsl:value-of select="zoz:DK"/></DU><!--??? nieje uplne jasne-->
								</xsl:when>
								<xsl:otherwise>
									<DU xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <NO><xsl:value-of select="zoz:UCEUCNA"/></NO>
							<xsl:choose>
								<xsl:when  test="zoz:SC != ''">
									<SC><xsl:value-of select="zoz:SC"/></SC>
								</xsl:when>
								<xsl:otherwise>
									<SC xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <NC><xsl:value-of select="zoz:UCEUENA"/></NC>
                            <NK><xsl:value-of select="zoz:ON"/></NK>
                            <NU><xsl:value-of select="zoz:ULIUINA"/></NU>
							<xsl:choose>
								<xsl:when  test="zoz:SI != ''">
									<ST><xsl:value-of select="zoz:SI"/></ST>
								</xsl:when>
								<xsl:otherwise>
									<ST xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <NS><xsl:value-of select="zoz:STASINA"/></NS>
                            <MP><xsl:value-of select="zoz:MP"/></MP>
                            <OP><xsl:value-of select="zoz:OP"/></OP>
                            <OO><xsl:value-of select="zoz:OO"/></OO>
                            <CC><xsl:value-of select="zoz:CO"/></CC>
                            <UM><xsl:value-of select="zoz:UM"/></UM>
                            <OS><xsl:value-of select="zoz:OI"/></OS>
                            <SI><xsl:value-of select="zoz:SS"/></SI>
                            <CU><xsl:value-of select="zoz:CB"/></CU>
							<xsl:choose>
								<xsl:when  test="zoz:PM != ''">
									<PM><xsl:value-of select="zoz:PM"/></PM>
								</xsl:when>
								<xsl:otherwise>
									<PM xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>                      
                            <PC><xsl:value-of select="zoz:PC"/></PC>
                            <CB><xsl:value-of select="zoz:CY"/></CB>
                            <IA><xsl:value-of select="zoz:IA"/></IA>
							<xsl:choose>
								<xsl:when  test="zoz:VD != ''">
									<VD><xsl:value-of select="zoz:VD"/></VD>
								</xsl:when>
								<xsl:otherwise>
									<VD xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:DI != ''">
									<DI><xsl:value-of select="zoz:DI"/></DI>
								</xsl:when>
								<xsl:otherwise>
									<DI xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:UI != ''">
									<UI><xsl:value-of select="zoz:UI"/></UI>
								</xsl:when>
								<xsl:otherwise>
									<UI xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:UE != ''">
									<CE><xsl:value-of select="zoz:UE"/></CE>
								</xsl:when>
								<xsl:otherwise>
									<CE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:UC != ''">
									<OA><xsl:value-of select="zoz:UC"/></OA>
								</xsl:when>
								<xsl:otherwise>
									<OA xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when  test="zoz:UL != ''">
									<UE><xsl:value-of select="zoz:UL"/></UE>
								</xsl:when>
								<xsl:otherwise>
									<UE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                            <OL><xsl:value-of select="zoz:OL"/></OL>
                            <REGList>
                                <xsl:for-each select="zoz:ZREList/zoz:ZRE">
                                <REG>
                                    <xsl:if test="@ID">
                                        <xsl:attribute name="ID">
                                            <xsl:value-of select="@ID"/>
                                        </xsl:attribute>
                                    </xsl:if>
									<xsl:choose>
										<xsl:when  test="zoz:PO != ''">
											<PO><xsl:value-of select="zoz:PO"/></PO>
										</xsl:when>
										<xsl:otherwise>
											<PO xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
									</xsl:choose>
                                    <RE><xsl:value-of select="zoz:RE"/></RE>
                                </REG>
                                </xsl:for-each>
                            </REGList>
                        </POB>
                        </xsl:for-each>
                    </POBList>
                    <ZPOList>
                        <xsl:for-each select="zoz:ZZPList/zoz:ZZP">
                            <ZPO>
                                <xsl:if test="@ID">
                                    <xsl:attribute name="ID">
                                        <xsl:value-of select="@ID"/>
                                    </xsl:attribute>
                                </xsl:if>
								<xsl:choose>
									<xsl:when  test="zoz:DZ != ''">
										<DZ><xsl:value-of select="zoz:DZ"/></DZ>
									</xsl:when>
									<xsl:otherwise>
										<DZ xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
									</xsl:otherwise>
								</xsl:choose>	
								<xsl:choose>
									<xsl:when  test="zoz:DK != ''">
										<DK><xsl:value-of select="zoz:DK"/></DK>
									</xsl:when>
									<xsl:otherwise>
										<DK xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
									</xsl:otherwise>
								</xsl:choose>
								<PO><xsl:value-of select="zoz:PO"/></PO>
                            <ZPZList>
                                <ZPZ>
                                    <xsl:if test="@ID">
                                        <xsl:attribute name="ID">
                                            <xsl:value-of select="@ID"/>
                                        </xsl:attribute>
                                    </xsl:if>
									<xsl:choose>
										<xsl:when  test="zoz:UC != ''">
											<UC><xsl:value-of select="zoz:UC"/></UC>
										</xsl:when>
										<xsl:otherwise>
											<UC xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
									</xsl:choose>
                                    <UCEUCNA><xsl:value-of select="zoz:UCEUCNA"/></UCEUCNA>
                                </ZPZ>
                            </ZPZList>
                        </ZPO>
                        </xsl:for-each>
                    </ZPOList>
                    <PZMList>
                        <PZM>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							
							<xsl:choose>
								<xsl:when  test="zoz:DP != ''">
									<DP><xsl:value-of select="zoz:DP"/></DP>
								</xsl:when>
								<xsl:otherwise>
									<DP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
								</xsl:otherwise>
							</xsl:choose>
                        </PZM>
                    </PZMList>
                    <DCDList>
                        <xsl:for-each select="zoz:ZDCList/zoz:ZDC">
                        <DCD>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:choose>
										<xsl:when  test="zoz:DD != ''">
											 <DD><xsl:value-of select="zoz:DD"/></DD>
										</xsl:when>
										<xsl:otherwise>
											<DD xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
                            <DDCDDNA><xsl:value-of select="zoz:DDCDDNA"/></DDCDDNA>
                            <CD><xsl:value-of select="zoz:CC"/></CD>
							<xsl:choose>
										<xsl:when  test="zoz:DU != ''">
											 <DU><xsl:value-of select="zoz:DU"/></DU>
										</xsl:when>
										<xsl:otherwise>
											<DU xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
                        </DCD>
                        </xsl:for-each>
                    </DCDList>
                    <USOList>
                        <xsl:for-each select="zoz:ZUSList/zoz:ZUS">
                        <USO>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:choose>
										<xsl:when  test="zoz:DP != ''">
											<DP><xsl:value-of select="zoz:DP"/></DP>
										</xsl:when>
										<xsl:otherwise>
											<DP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
										<xsl:when  test="zoz:UD != ''">
											<UD><xsl:value-of select="zoz:UD"/></UD>
										</xsl:when>
										<xsl:otherwise>
											<UD xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
                        </USO>
                        </xsl:for-each>
                    </USOList>
                    <SNRList>
                        <xsl:for-each select="zoz:ZHSList/zoz:ZHS">
                        <SNR>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:choose>
										<xsl:when  test="zoz:SA != ''">
											<SN><xsl:value-of select="zoz:SA"/></SN>
										</xsl:when>
										<xsl:otherwise>
											<SN xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
                            <SNPSNNA><xsl:value-of select="zoz:SNPSANA"/></SNPSNNA>
							
							<xsl:choose>
										<xsl:when  test="zoz:DZ != ''">
											<DZ><xsl:value-of select="zoz:DZ"/></DZ>
										</xsl:when>
										<xsl:otherwise>
											<DZ xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
										<xsl:when  test="zoz:DK != ''">
											<DK><xsl:value-of select="zoz:DK"/></DK>
										</xsl:when>
										<xsl:otherwise>
											<DK xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
                            <PZ><xsl:value-of select="zoz:PO"/></PZ>
                        </SNR>
                        </xsl:for-each>
                    </SNRList>
                    <ZUDList>
                        <xsl:for-each select="zoz:ZUDList/zoz:ZUD">
                            <ZUD>
                            <xsl:if test="@ID">
                                <xsl:attribute name="ID">
                                    <xsl:value-of select="@ID"/>
                                </xsl:attribute>
                            </xsl:if>
							<xsl:choose>
										<xsl:when  test="zoz:ZO != ''">
											<ZO><xsl:value-of select="zoz:ZO"/></ZO>
										</xsl:when>
										<xsl:otherwise>
											<ZO xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
										</xsl:otherwise>
							</xsl:choose>
                            <ZOBZONA><xsl:value-of select="zoz:ZOBZONA"/></ZOBZONA>
                        </ZUD>
                        </xsl:for-each>
                    </ZUDList>
                </OEX>
                </xsl:for-each>
            </OEXList>
        </POV>
    </xsl:template>
</xsl:stylesheet>
