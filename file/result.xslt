<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl js" 
                xmlns="http://www.w3.org/1999/xhtml" xmlns:js="urn:custom-javascript" 
                xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
    <xsl:template match="/">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                <style>
                    @page{
                        size: A4
                        margin: 0;					
                    }
                    
                    *{
                        margin: 0;
                        padding: 0;
                    }
                    
                    a{
                        text-decoration: none;
                    }
            
                    p{
                        margin: 0px;
                        padding:0px;
                    }
            
                    html,body {
                        color: #000;
                        font-size: 8pt;
                        margin: 0 auto;
                        line-height: 16pt;
						font-family: "Times New Roman", Times, serif;
                    }
                    
                    .wrapper{
                        margin: 0 auto;
                        width: auto;
                        height: auto;
                        padding: 0;
                    }
                    
                    .wrapper-inner{
                        background-color: #fff
                        padding: 0;
                        margin: 0;
                    }
                    
                    table{
                        margin: 0 auto;
                        padding: 0;
                    }
                    

                </style>
            </head>
            <body>
                <div class="page">
                    <div class="wrapper">
                        <div class="wrapper-inner">
                            <table style="width:100%;">
                                <tr>
                                    <td style="text-align: center;">
                                        <p style="font-size: 13px;font-weight: bold;">
                                            CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM
                                        </p>
                                        <p style="font-size: 13px;font-weight: bold;">
                                            Độc lập – Tự do – Hạnh phúc
                                        </p>
                                        <p style="font-size: 15px;font-weight: bold;">
                                            HỢP ĐỒNG LAO ĐỘNG
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; font-style: italic;">
                                            Số: 01/MOBILE-ID-VHB/HDLD
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify;">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Chúng tôi, một bên là:
                                        </p>
                                    </td>
                                    <td style="width:80%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            CÔNG TY CỔ PHẦN CÔNG NGHỆ VÀ DỊCH VỤ MOBILE-ID
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Địa chỉ:
                                        </p>
                                    </td>
                                    <td style="width:80%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            19 Đặng Tiến Đông, Phường An Phú, Thành phố Thủ Đức, Thành phố Hồ Chí Minh
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                           Điện thoại:
                                        </p>
                                    </td>
                                    <td style="width:80%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            028. 3636 6015
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Đại diện:
                                        </p>
                                    </td>
                                    <td style="width:50%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Ông PHẠM XUÂN KHÁNH
                                        </p>
                                    </td>
                                    <td style="width:30%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Quốc tịch: Việt Nam
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                           Chức vụ:
                                        </p>
                                    </td>
                                    <td style="width:80%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            TỔNG GIÁM ĐỐC
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Và một bên là Ông/Bà:
                                        </p>
                                    </td>
                                    <td style="width:80%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold; text-transform: uppercase;">
                                            <!--VƯƠNG HOÀNG BẢO-->
                                            <xsl:value-of select="KYC/FullName" />
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                           Sinh ngày:
                                        </p>
                                    </td>
                                    <td style="width:50%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            <!--03/03/1999-->
                                            <xsl:value-of select="KYC/BirthDate" />
                                        </p>
                                    </td>
                                    <td style="width:30%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Quốc tịch: <xsl:value-of select="KYC/Nationality" />
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Số CCCD:
                                        </p>
                                    </td>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            <!--272706245-->
                                            <xsl:value-of select="KYC/PersonalNumber" />
                                        </p>
                                    </td>
                                    <td style="width:30%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Ngày cấp: <xsl:value-of select="KYC/IssuanceDate" />
                                        </p>
                                    </td>
                                    <td style="width:30%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Nơi cấp: CTCCSQLHCVTTXH
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:20%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Địa chỉ thường trú: 
                                        </p>
                                    </td>
                                    <td style="width:80%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            <!--Ấp Cầm Đường, Bình Sơn, Long Thành, Đồng Nai-->
                                            <xsl:value-of select="KYC/PlaceOfResidence" />
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width:100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: 350">
                                            Thoả thuận ký kết hợp đồng lao động và cam kết làm đúng những điều khoản sau đây:
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width: 100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Điều 1: Thời hạn và công việc hợp đồng 
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Loại hợp đồng lao động: Hợp đồng lao động xác định thời hạn 12 tháng
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 129px">
                                            Bắt đầu từ <xsl:value-of select="KYC/CurrentDate" /> đến <xsl:value-of select="KYC/DateAfterOneYear" />.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Địa điểm làm việc: 19 Đặng Tiến Đông, Phường An Phú, Thành phố Thủ Đức, Thành phố Hồ Chí Minh
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chức danh chuyên môn: Nhân viên Kỹ thuật.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Công việc phải làm: Tùy theo sự điều động của cấp trên.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width: 100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Điều 2: Chế độ làm việc: 
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Thời giờ làm việc: giờ bắt buộc từ thứ 2 đến thứ 6: 8:30 – 12:00; 13:30 – 18:00
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Thời gian nghỉ ngơi: từ thứ 2 đến thứ 6: 12:01 – 13:29
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Được cấp phát những dụng cụ làm việc gồm: theo quy định của Công Ty cho công việc được giao
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width: 100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Điều 3: Nghĩa vụ và quyền lợi của người lao động:
                                        </p>
                                        <p style="font-size: 11px;font-weight: bold; margin-left: 15px;">
                                            1. Quyền lợi:
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Phương tiện đi lại làm việc: người lao động tự lo liệu.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Mức lương chính hoặc tiền công: 7.000.000 VNĐ/tháng.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hình thức trả lương: Chuyển khoản/ Tiền mặt.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Thời gian trả lương: vào ngày cuối cùng của mỗi tháng hoặc theo thỏa thuận với người lao động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Phụ cấp lương KPI, lương bổ sung: theo tính chất công việc và theo năng lực, dự án của từng cá nhân, theo quy định của Công ty.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hỗ trợ cơm trưa: 1.050.000 VNĐ/tháng. Nếu có thay đổi, sẽ được quy định trong phụ lục hợp đồng hoặc theo quyết định của công ty.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hỗ trợ điện thoại: 300.000 VNĐ/tháng. Nếu có thay đổi, sẽ được quy định trong phụ lục hợp đồng hoặc theo quyết định của công ty.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hỗ trợ xăng xe và nhà ở: 2.200.000 VNĐ/tháng. Nếu có thay đổi, sẽ được quy định trong phụ lục hợp đồng hoặc theo quyết định của công ty.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Phụ cấp khác: theo tính chất công việc và theo năng lực, dự án của từng cá nhân, theo quy định của Công ty. 
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chế độ thưởng: tùy theo quy định của công ty.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chế độ nâng lương: theo quy định của Bộ Luật Lao Động và công ty tại từng thời điểm và theo năng lực của người lao động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chế độ thăng chức: tùy thuộc vào kết quả làm việc của người lao động và phát triển của công ty.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Được trang bị bảo hộ lao động gồm: không áp dụng.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chế độ nghỉ ngơi (nghỉ việc riêng, phép năm, lễ tết...):
                                        </p>
                                   
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 30px">
                                            <span style="font-weight: bold">&#10003;</span> Nghỉ lễ: theo quy định của Bộ Luật Lao Động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 30px">
                                            <span style="font-weight: bold">&#10003;</span> Nghỉ phép: theo quy định của Bộ Luật Lao Động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 30px">
                                            <span style="font-weight: bold">&#10003;</span> Nghỉ việc riêng: theo quy định của Bộ Luật Lao Động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - BHXH: Theo quy định của Bộ Luật Lao Động và Luật BHXH. 
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chế độ đào tạo: Người lao động sẽ được công ty tạo điều kiện để tham gia các chương trình đào tạo trong và ngoài trụ sở công ty, thực tập trong các lĩnh vực khác chuyên môn để mở rộng kiến thức và nâng cao trình độ.Trong trường hợp người lao động không đảm nhận vị trí hiện tại thì phải bàn giao công việc cho người kế nhiệm được công ty phân công trong thời gian 1 tháng sau khi nhận được thông báo dừng công tác tại vị trí đảm nhiệm.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Những thoả thuận khác:
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 30px">
                                            <span style="font-weight: bold">&#10003;</span>
                                            <span> Điều chuyển nhân viên: theo thỏa thuận.</span>
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 30px">
                                            <span style="font-weight: bold">&#10003;</span>
                                            <span> Người sử dụng lao động sẽ khấu trừ thuế thu nhập cá nhân (nếu có) từ thu nhập hàng tháng /năm của người lao động để nộp cho cơ quan thuế. Cuối năm, người sử dụng lao động sẽ quyết toán thuế thu nhập cá nhân cho người lao động hoặc người lao động tự quyết toán với cơ quan thuế.</span>
                                        </p>

                                        <p style="font-size: 11px;font-weight: bold; margin-left: 15px;">
                                            2. Nghĩa vụ:
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hoàn thành những công việc đã cam kết trong hợp đồng lao động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Chấp hành lệnh điều hành sản xuất - kinh doanh, nội quy kỷ luật lao động, an toàn lao động. 
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Bồi thường vi phạm và vật chất: Theo quy định của Luật Lao Động, nội quy và các quy định, chính sách khác của công ty. 
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width: 100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Điều 4. Nghĩa vụ và quyền hạn của người sử dụng lao động
                                        </p>
                                        <p style="font-size: 11px;font-weight: bold; margin-left: 15px;">
                                            1. Nghĩa vụ:
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Bảo đảm việc làm và thực hiện đầy đủ những điều đã cam kết trong hợp đồng lao động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Thanh toán đầy đủ, đúng thời hạn các chế độ và quyền lợi cho người lao động theo hợp đồng lao động, thoả ước lao động tập thể (nếu có).
                                        </p>
                                        <p style="font-size: 11px;font-weight: bold; margin-left: 15px;">
                                            2. Quyền hạn:
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Điều hành người lao động hoàn thành công việc theo hợp đồng (bố trí, điều chuyển, tạm ngừng việc...).
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Tạm hoãn, chấm dứt hợp đồng lao động, kỷ luật người lao động theo quy định của pháp luật, thoả ước lao động tập thể (nếu có) và nội quy lao động của doanh nghiệp.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width: 100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Điều 5. Chấm dứt hợp đồng lao động
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; margin-left: 15px;">
                                            Hợp đồng lao động này sẽ chấm dứt trong những trường hợp theo quy định trong Bộ Luật Lao Động và quy định của công ty. Hoặc theo sự thỏa thuận của 2 bên.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                            <table style="width:100%;">
                                <tr>
                                    <td style="width: 100%; text-align: justify">
                                        <p style="font-size: 11px;font-weight: bold">
                                            Điều 6. Điều khoản thi hành
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Những vấn đề về lao động không ghi trong hợp đồng lao động này thì áp dụng quy định của thoả ước tập thể, trường hợp chưa có thoả ước tập thể thì áp dụng quy định của pháp luật lao động.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hợp đồng lao động được làm thành 02 bản có giá trị ngang nhau, mỗi bên giữ một bản và có hiệu lực từ ngày ký. Khi hai bên ký kết phụ lục hợp đồng lao động thì nội dung của phụ lục hợp đồng lao động cũng có giá trị như các nội dung của bản hợp đồng lao động này.
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350">
                                            - Hợp đồng này làm tại CÔNG TY CỔ PHẦN CÔNG NGHỆ VÀ DỊCH VỤ MOBILE-ID ngày <xsl:value-of select="KYC/PreviousDay" /> tháng <xsl:value-of select="KYC/PreviousMonth" /> năm <xsl:value-of select="KYC/PreviousYear" />.
                                        </p>
                                    </td>
                                </tr>
                            </table> 
                            
                            
                            <table style="width:100%; padding-top: 20px">
                                <tr>
                                    <td style="width: 50%; text-align: center">
                                        <p style="font-size: 11px;font-weight: bold;">
                                            NGƯỜI LAO ĐỘNG
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; font-style: italic;">
                                             (Ký, ghi rõ họ tên)  
                                        </p>
                                    </td>
                                    <td style="width: 50%; text-align: center">
                                        <p style="font-size: 11px;font-weight: bold;">
                                            NGƯỜI SỬ DỤNG LAO ĐỘNG
                                        </p>
                                        <p style="font-size: 11px;font-weight: 350; font-style: italic;">
                                            (Ký, ghi rõ họ tên, đóng dấu) 
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td >
                                        <div style="height: 4cm;"></div>
                                    </td>
                                    <td >
                                        <div style="height: 4cm;"></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="text-align: center;">
                                        <p style="font-size: 11px;font-weight: bold; text-transform: uppercase;">
                                            <xsl:value-of select="KYC/FullName" />
                                        </p>
                                    </td>
                                    <td style="text-align: center;">
                                        <p style="font-size: 11px;font-weight: bold;">
                                             PHẠM XUÂN KHÁNH
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                        </div>
                    </div>
                </div>
            </body>
        </html>

    </xsl:template>
</xsl:stylesheet>

