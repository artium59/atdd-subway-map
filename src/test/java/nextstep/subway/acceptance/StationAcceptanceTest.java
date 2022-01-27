package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> createResponse = 지하철역_생성();

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * Given 새로운 지하철역 생성을 요청 하고
     * When 지하철역 목록 조회를 요청 하면
     * Then 두 지하철역이 포함된 지하철역 목록을 응답받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void getStations() {
        /// given
        String 역삼역 = "역삼역";
        String 강남역 = "강남역";
        지하철역_생성(강남역);
        지하철역_생성(역삼역);

        // when
        ExtractableResponse<Response> readResponse = 지하철역_목록_조회();

        assertThat(readResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = readResponse.jsonPath().getList("name");
        assertThat(stationNames).contains(강남역, 역삼역);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> deleteResponse = 지하철역_삭제(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 같은 이름의 지하철역 생성을 요청 하면
     * Then 지하철 생성 요청이 실패한다.
     */
    @DisplayName("지하철역 등록 실패 - 이름 중복")
    @Test
    void createStationWithDuplicatedException() {
        // given
        지하철역_생성("용산역");

        // when
        ExtractableResponse<Response> createResponse = 지하철역_생성("용산역");

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
