package com.icomp.Iswtmv10.internet;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by Think on 2016/9/12.
 */
public interface IRequest {
    /**
     * 新刀入库-扫描标签取得钻头(刀片)的入库信息
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getToolInfoByRfid")
    Call<String> getContentScanXinDao(@Field("rfidCode") String code);

    /**
     * 新刀入库-根据输入的材料号查询钻头（刀片）的入库信息
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getToolInfoByToolCode")
    Call<String> getCpmtemtSearchXinDao(@Field("materialNum") String code);


    /**
     * 新刀入库-提交入库数量信息
     * @param customerId
     * @param materialNum
     * @param toolsOrdeNO
     * @param storageNum
     * @param toolID
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveToolInputInfo")
    Call<String> saveToolInputInfo(@Field("customerID") String customerId,
                                   @Field("materialNum") String materialNum,
                                   @Field("toolsOrdeNO") String toolsOrdeNO,
                                   @Field("storageNum") String storageNum,
                                   @Field("toolID") String toolID,
                                   @Field("valType") String valType,
                                   @Field("poItem") String poItem);


    /**
     * 换领出库-取得要换领出库的刀具信息
     * @param materialNum
     * @param rfidCode
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getFRedemptionapplyInfo")
    Call<String> getFRedemptionapplyInfo(@Field("materialNum") String materialNum,
                                         @Field("rfidCode") String rfidCode);

    /**
     * 换领出库-提交换领出库的刀具信息
     * @param redemptionApplyList
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveFRedemptionapplyInfo")
    Call<String> saveFRedemptionapplyInfo(@Field("redemptionApplyList") String redemptionApplyList, @Field("costCenter") String costCenter);

    /**
     * 补领出库-获取换领出库申请人信息
     * @return
     */
    @POST("/dazhong/getFReplacementList")
    Call<String> getFReplacementList();


    /**
     * 补领出库-取得换领申请人申请详细信息
     * @param applyID
     * @param applyTime
     * @param replacementFlag
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getFReplacementApply")
    Call<String> getFReplacementApply (@Field("applyID") String applyID,
                                       @Field("applyTime") String applyTime,
                                       @Field("replacementFlag") String replacementFlag);


    /**
     * 补领出库-提交换领申请人申请详细信息
     * @param replaceJsonList
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveFReplacementapplyInfo")
    Call<String> saveFReplacementapplyInfo (@Field("replaceJsonList") String replaceJsonList, @Field("costCenter") String costCenter);


    /**
     * 刀具报废-单品刀具报废
     * @param toolCodes
     * @param scrapNumbers
     * @param customerID
     * @param scrapState
     * @param scrapCause
     * @param handSetId
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveFScrap")
    Call<String> saveFScrap (@Field("toolCodes") String toolCodes,
                             @Field("scrapNumbers") String scrapNumbers,
                             @Field("customerID") String customerID,
                             @Field("scrapState") String scrapState,
                             @Field("scrapCause") String scrapCause,
                             @Field("handSetId") String handSetId,
                             @Field("scrapCode") String scrapCode);


    /**
     * 刀具报废-扫码查询一体刀信息
     * @param rfidCode
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getComposeInfo")
    Call<String> getComposeInfo (@Field("rfidCode") String rfidCode);


    @FormUrlEncoded
    @POST("/dazhong/saveComposeScrap")
    Call<String> saveComposeScrap (@Field("toolCodes") String toolCodes,
                                   @Field("rfidContainerIDs") String rfidContainerIDs,
                                   @Field("customerID") String customerID,
                                   @Field("scrapState") String scrapState,
                                   @Field("scrapCause") String scrapCause,
                                   @Field("handSetId") String handSetId);

    /**
     * 刀具报废-扫码查询筒刀信息
     * @param rfidCode
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getTubeInfo")
    Call<String> getTubeInfo (@Field("rfidCode") String rfidCode);


    /**
     * 刀具报废-筒刀报废
     * @param toolCodes
     * @param rfidContainerIDs
     * @param synthesisParametersCodes
     * @param customerID
     * @param scrapState
     * @param scrapCause
     * @param handSetId
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveTubeScrap")
    Call<String> saveTubeScrap (@Field("toolCodes") String toolCodes,
                                @Field("rfidContainerIDs") String rfidContainerIDs,
                                @Field("synthesisParametersCodes") String synthesisParametersCodes,
                                @Field("customerID") String customerID,
                                @Field("scrapState") String scrapState,
                                @Field("scrapCause") String scrapCause,
                                @Field("handSetId") String handSetId,
                                @Field("toolCounts") String toolCounts);

    /**
     * 刀具换装-扫码查询合成刀信息
     * @param rfidCode
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getSynthesisToolOneKnifeInfo")
    Call<String> getSynthesisToolOneKnifeInfo(@Field("rfidCode") String rfidCode);

    /**
     * 刀具换装-查询合成刀详细信息
     * @param spCodes
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getSynthesisTool")
    Call<String> getSynthesisTool(@Field("spCodes") String spCodes);

    /**
     * 刀具换装-提交换装刀具信息
     * @param toolCodes
     * @param changeNumbers
     * @param lostNumbers
     * @param customerID
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveHToolInfo")
    Call<String> saveHToolInfo(@Field("toolCodes") String toolCodes,
                               @Field("changeNumbers") String changeNumbers,
                               @Field("lostNumbers") String lostNumbers,
                               @Field("customerID") String customerID,
                               @Field("synthesisParametersCodes") String synthesisParametersCodes,
                               @Field("rfidContainerIDs") String rfidContainerIDs,
                               @Field("authorizationFlgs") String authorizationFlgs,
                               @Field("authorizationUserID") String authorizationUserID);


    /**
     * 刀具拆分-扫码查询合成刀信息
     * @param rfidCode
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/getSynthesisToolSplit")
    Call<String> getSynthesisToolSplit(@Field("rfidCode") String rfidCode);

    /**
     * 刀具拆分-提交拆分信息
     * @param synthesisParametersCodes
     * @param rfidContainerIDs
     * @param toolConsumetypes
     * @param customerID
     * @return
     */
    @FormUrlEncoded
    @POST("/dazhong/saveSynthesisToolSplit")
    Call<String> saveSynthesisToolSplit(@Field("synthesisParametersCodes") String synthesisParametersCodes,
                                        @Field("rfidContainerIDs") String rfidContainerIDs,
                                        @Field("toolConsumetypes") String toolConsumetypes,
                                        @Field("customerID") String customerID,
                                        @Field("codes") String codes,
                                        @Field("authorizationUserID") String authorizationUserID);

    /**
     * 刀具组装-扫码查询合成刀信息
     * @param rfidCode
     */
    @FormUrlEncoded
    @POST("/dazhong/getSynthesisToolInstall")
    Call<String> getSynthesisToolInstall(@Field("rfidCode") String rfidCode,
                                         @Field("flg") String flg);

    /**
     * 刀具组装-提交组装信息
     * @param synthesisParametersCodes
     * @param rfidContainerIDs
     * @param toolConsumetypes
     * @param customerID
     */
    @FormUrlEncoded
    @POST("/dazhong/saveSynthesisToolInstall")
    Call<String> saveSynthesisToolInstall(@Field("synthesisParametersCodes") String synthesisParametersCodes,
                                        @Field("rfidContainerIDs") String rfidContainerIDs,
                                        @Field("toolConsumetypes") String toolConsumetypes,
                                        @Field("customerID") String customerID,
                                          @Field("installparams") String installparams);

    /**
     * 刀具组装-提交组装信息,组装成新刀
     * rfidCode:RFID标签ID
     toolCode:材料号
     customerID：用户ID
     */
    @FormUrlEncoded
    @POST("/dazhong/saveSynthesisToolInstallNewTool")
    Call<String> saveSynthesisToolInstall(@Field("rfidCode") String rfidCode,
                                          @Field("toolCode") String toolCode,
                                          @Field("customerId") String customerId);

    /**
     *
     */
    @POST("/dazhong/getScrapStateList")
    Call<String> getScrapStateList();
    /**          pig Start          **/


    /**          FanLL Start          **/
    //C01S017回厂入库--查询输入的材料号是否可以回厂（废弃）
    @FormUrlEncoded
    @POST("/dazhong/getFBackFactoryToolInfo")
    Call<String> getFBackFactoryToolInfo(@Field("materialNum") String materialNum);

    //C01S017回厂确认--查询扫描的标签是否可以回厂（废弃）
    @FormUrlEncoded
    @POST("/dazhong/getBackFactoryToolInfo")
    Call<String> getBackFactoryToolInfo(@Field("rfidCode") String rfidString);

    //C01S017回厂确认--提交回厂刀具信息（废弃）
    @FormUrlEncoded
    @POST("/dazhong/saveFBackFactoryToolInfo")
    Call<String> saveFBackFactoryToolInfo(@Field("backFactoryJsonList") String backFactoryJsonList);

    //C01S017回厂确认--查询所有单号列表
    @POST("/dazhong/getOutFactoryOutDoorNomList")
    Call<String> getOutFactoryOutDoorNomList();

    //C01S017回厂确认--根据单号查厂外修复信息
    @FormUrlEncoded
    @POST("/dazhong/getOutFactoryDescByOutDoorNom")
    Call<String> getOutFactoryDescByOutDoorNom(@Field("outDoorNom") String outDoorNom);

    //C01S018厂内修磨--单品修磨
    @FormUrlEncoded
    @POST("/dazhong/saveGrindingToolInfo")
    Call<String> saveGrindingToolInfo(@Field("toolCodes") String toolCodes, @Field("numbers") String numbers, @Field("customerID") String customerID);

    //C01S018厂内修磨--根据材料号查询一体刀信息
    @FormUrlEncoded
    @POST("/dazhong/getOneKnifeInfo")
    Call<String> getOneKnifeInfo(@Field("rfidCode") String rfidString);

    //C01S018厂内修磨--提交要修磨的一体刀信息
    @FormUrlEncoded
    @POST("/dazhong/saveGrindingOneKnifeInfo")
    Call<String> saveGrindingOneKnifeInfo(@Field("toolCodes") String toolCodes, @Field("rfidContainerIDs") String rfidContainerIDs,
                                          @Field("authorizationFlgs") String authorizationFlgs, @Field("customerID") String customerID, @Field("gruantUserID") String gruantUserID);

    //C01S019厂外修磨--查询刀具信息判断是否可以出厂修磨
    @FormUrlEncoded
    @POST("/dazhong/getOutFactoryToolInfo")
    Call<String> getOutFactoryToolInfo(@Field("rfidCode") String rfidString, @Field("materialNum") String materialNum);

    //C01S019厂外修磨--查询厂外修复商家list
    @POST("/dazhong/getMerchantsList")
    Call<String> getMerchantsList();

    //C01S019厂外修磨--提交厂外修磨信息
    @FormUrlEncoded
    @POST("/dazhong/saveOutFactoryToolInfo")
    Call<String> saveOutFactoryToolInfo(@Field("toolCodes") String toolCodes, @Field("toolIDs") String toolIDs, @Field("toolTypes") String toolTypes,
                                        @Field("rfidContainerIDs") String rfidContainerIDs, @Field("numbers") String numbers,
                                        @Field("customerID") String customerID, @Field("merchantsID") String merchantsID,
                                        @Field("authorizationFlgs") String authorizationFlgs);

    //C01S023单品绑定--查询激光码信息
    @FormUrlEncoded
    @POST("/dazhong/getOneKnifeBinding")
    Call<String> getOneKnifeBinding(@Field("materialNum") String materialNum, @Field("manufactor") String manufactor);

    //C01S023单品绑定--查询平均加工量
    @FormUrlEncoded
    @POST("/dazhong/getToolDurableList")
    Call<String> getToolDurableList(@Field("materialNum") String materialNum);

    //C01S023单品绑定--提交单品绑定信息
    @FormUrlEncoded
    @POST("/dazhong/saveOneKnifeBinding")
    Call<String> saveOneKnifeBinding(@Field("materialNum") String materialNum, @Field("laserCode") String laserCode,
                                     @Field("rfidCode") String rfidCode, @Field("customerID") String customerID,
                                     @Field("grindingCount") String grindingCount, @Field("toolDurable") String toolDurable);

    //C03S001合成刀具初始化--查询合成刀具组成信息
    @FormUrlEncoded
    @POST("/dazhong/seachFInitSynthesisByRfid")
    Call<String> seachFInitSynthesisByRfid(@Field("materialNum") String materialNum, @Field("rfidCode") String rfidCode,
                                           @Field("laserCode") String laserCode, @Field("flg") String flg);

    //C03S001合成刀具初始化--验证要初始化的合成刀具标签
    @FormUrlEncoded
    @POST("/dazhong/checkInitSynthesis")
    Call<String> checkInitSynthesis(@Field("rfidCode") String rfidCode, @Field("spCode") String spCode);

    //C03S001合成刀具初始化--提交初始化合成刀具RFIDCodeList
    @FormUrlEncoded
    @POST("/dazhong/submitFInitSynthesis")
    Call<String> submitFInitSynthesis(@Field("rfidCode") String rfidCode, @Field("spCode") String spCode, @Field("createType") String createType,
                                      @Field("customerId") String customerId, @Field("handSetId") String handSetId, @Field("flg") String flg,
                                      @Field("synthesisflg") String synthesisflg, @Field("laserCode") String laserCode);

    //C03S002库位标签初始化--按材料号查询库存信息
    @FormUrlEncoded
    @POST("/dazhong/findLibraryCodeMsg")
    Call<String> findLibraryCodeMsg(@Field("toolCode") String toolCode);

    //C03S002库位标签初始化--查询当前标签是否已初始化
    @FormUrlEncoded
    @POST("/dazhong/findLibraryInitializeMsg")
    Call<String> findLibraryInitializeMsg(@Field("toolCode") String toolCode, @Field("rfidCode") String rfidCode);

    //C03S002库位标签初始化--提交库位标签初始化数据
    @FormUrlEncoded
    @POST("/dazhong/submitLibraryCodeMsg")
    Call<String> submitLibraryCodeMsg(@Field("toolCode") String toolCode, @Field("rfidCode") String rfidCode, @Field("userID") String userID,
                                      @Field("knifelnventoryNumber") String knifelnventoryNumber);


    /**          PigLL End          **/

}
